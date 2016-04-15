package com.mfp.ali;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import com.aliyun.api.AliyunClient;
import com.aliyun.api.DefaultAliyunClient;
import com.aliyun.api.domain.Disk;
import com.aliyun.api.domain.Eip;
import com.aliyun.api.domain.Image;
import com.aliyun.api.domain.Instance;
import com.aliyun.api.domain.SecurityGroup;
import com.aliyun.api.domain.Snapshot;
import com.aliyun.api.domain.VSwitch;
import com.aliyun.api.domain.Vpcs;
import com.mfp.ali.oper.DiskOperator;
import com.mfp.ali.oper.EipAddressOperator;
import com.mfp.ali.oper.ImageOperator;
import com.mfp.ali.oper.InstanceOperator;
import com.mfp.ali.oper.SecurityGroupOperator;
import com.mfp.ali.oper.SnapshotOperator;
import com.mfp.ali.oper.VPCOperator;
import com.mfp.ali.oper.VSwitchOperator;
import com.mfp.ali.util.ClientAdapter;
import com.mfp.ali.util.Formatter;
import com.mfp.ali.util.PropertiesLoader;
import com.taobao.api.ApiException;

/**
 * AliGo.main(new String[]{"E:/example.properties", "destroy"});
 * AliGo.main(new String[]{"E:/example.properties", "create"});
 * AliGo.main(new String[]{"/home/whb/ali/example.properties", "4"});
 * 
 * java -jar AliGo.jar /home/whb/ali/example.properties 3 >> ali.log 2>&1
 * 0 3 29 mar * java -jar AliGo.jar /home/whb/ali/example.properties 3 >> ali.log 2>&1
 * 
 * @author
 *
 */
public class AliGo {
    
    private static Logger logger = Logger.getLogger(AliGo.class.getName());
    
    private static int instance_max_index = 100;
    
    public static void main(String[] args){
        String cfg = null;
        String cmd = null;
        
        if(args != null && args.length >= 2){
            cfg = args[0];
            cmd = args[1];
        }
        
        if(cfg == null && cmd == null){
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Enter config file: ");
            cfg = scanner.nextLine();
            
            System.out.println("Enter command number or name. ");
            System.out.println("1: create");
            System.out.println("2: destroy");
            System.out.println("3: describe");
            System.out.println("4: force_create_snapshot");
            System.out.println("5: force_create_image");
            cmd = scanner.nextLine();
            
            scanner.close();
        }
        
        logger.info("Config：" + cfg);
        logger.info("Command：" + cmd);
        
        try {
            if("create".equals(cmd) || "1".equals(cmd)){
                create(cfg);
            }else if("destroy".equals(cmd) || "2".equals(cmd)){
                destroy(cfg);
            }else if("describe".equals(cmd) || "3".equals(cmd)){
                describe(cfg);
            }else if("force_create_snapshot".equals(cmd) || "4".equals(cmd)){
                forceCreateSnapshot(cfg);
            }else if("force_create_image".equals(cmd) || "5".equals(cmd)){
                forceCreateImage(cfg);
            }else{
                logger.info("Exit：Nothing happened.");
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void create(String cfg) throws ApiException{
        logger.info("Creating......");
        
        PropertiesLoader properties = loadProperties(cfg);
        
        ClientAdapter client = initClientAdapter(properties);
        
        Vpcs vpcs = createVpcs(properties, client);
        
        createVSwitches(properties, client, vpcs);
        
        SecurityGroup securityGroup = createSecurityGroup(properties, client, vpcs);
        
        createSnapshots(properties, client, false, false);
        
        createImages(properties, client, false);
        
        List<Instance> list = createInstances(properties, client, vpcs, securityGroup);
        
        createAndAssociateEip(properties, client, vpcs);
        
        startInstances(properties, client);
        
        logger.info("Create completely!!");
        
        logger.info("************************* Instance List ******************************");
        logger.info(Formatter.beanToJsonString(list));
        logger.info("**********************************************************************");
    }
    
    public static void destroy(String cfg) throws ApiException{
        logger.info("Destroy......");
        
        PropertiesLoader properties = loadProperties(cfg);
        
        ClientAdapter client = initClientAdapter(properties);

        unassociateAndDesdroyEip(properties, client);
        
        stopInstances(properties, client);
        
        destroyInstances(properties, client);
        
        destroySecurityGroup(properties, client);
        
        destroyVSwitches(properties, client);
        
        destroyVPC(properties, client);
        
        logger.info("Destroy completely!!");
    }
    
    public static void describe(String cfg) throws ApiException{
        logger.info("Describe......");
        
        PropertiesLoader properties = loadProperties(cfg);
        
        ClientAdapter client = initClientAdapter(properties);

        // new operator
        InstanceOperator instanceOper = new InstanceOperator(client);
        DiskOperator diskOper = new DiskOperator(client);
        SnapshotOperator snapshotOper = new SnapshotOperator(client);
        ImageOperator imageOper = new ImageOperator(client);
        
        String regionId_tbc_general = properties.getPropertyValue("Instance.general.Copy.RegionId");
        
        //get instance, system disk and data disks.
        List<Instance> instances = instanceOper.getAll(regionId_tbc_general);
        for(Instance instance: instances){
            logger.info("*************************** Instance (" + instance.getInstanceId() + ") " + instance.getInnerIpAddress() + " **************************");
            logger.info("Instance >>> " + Formatter.beanToJsonString(instance));
            
            Disk sysDisk_tbc = diskOper.getInstanceSystemDisk(regionId_tbc_general, instance.getInstanceId());
            logger.info("System Disk >>> " + Formatter.beanToJsonString(sysDisk_tbc));
            
            List<Disk> dataDisks_tbc = diskOper.getInstanceDataDisks(regionId_tbc_general, instance.getInstanceId());
            logger.info("Data Disks >>> " + Formatter.beanToJsonString(dataDisks_tbc));
            
            Snapshot snapshot = snapshotOper.getLatest(regionId_tbc_general, instance.getInstanceId(), sysDisk_tbc.getDiskId());
            logger.info("System Disk Latest Snapshot >>> " + Formatter.beanToJsonString(snapshot));
            
            String imageName = instance.getInstanceName() + "_image";
            Image image = imageOper.getLatest(instance.getRegionId(), imageName);
            logger.info("System Disk Latest Image >>> " + Formatter.beanToJsonString(image));
            
            for(Disk disk: dataDisks_tbc){
                Snapshot snapshot_data = snapshotOper.getLatest(regionId_tbc_general, instance.getInstanceId(), disk.getDiskId());
                logger.info("Data Disk Latest Snapshot (" + disk.getDiskId() + ") >>> " + Formatter.beanToJsonString(snapshot_data));
            }
        }
        
        logger.info("Describe completely!!");
    }
    
    public static void forceCreateSnapshot(String cfg) throws ApiException{
        logger.info("Force create snapshots......");
        
        PropertiesLoader properties = loadProperties(cfg);
        
        ClientAdapter client = initClientAdapter(properties);
        
        createSnapshots(properties, client, true, true);
        
        logger.info("Force create snapshots completely!!");
    }
    
    public static void forceCreateImage(String cfg) throws ApiException{
        logger.info("Force create snapshots and images......");
        
        PropertiesLoader properties = loadProperties(cfg);
        
        ClientAdapter client = initClientAdapter(properties);
        
        createImages(properties, client, true);
        
        logger.info("Force create completely!!");
    }
    
    private static PropertiesLoader loadProperties(String cfg) throws ApiException{
        PropertiesLoader properties = new PropertiesLoader();
        properties.loadProperties(cfg, "UTF-8");
        return properties;
    }
    
    private static ClientAdapter initClientAdapter(PropertiesLoader properties) throws ApiException{
        AliyunClient client = new DefaultAliyunClient(
                properties.getPropertyValue("ali.sendUrl"), 
                properties.getPropertyValue("ali.accessKeyId"), 
                properties.getPropertyValue("ali.accessKeySecret"));
        ClientAdapter clientAdapter = new ClientAdapter(client);
        return clientAdapter;
    }
    
    private static Vpcs createVpcs(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        Vpcs model = new Vpcs();
        model.setVpcName(properties.getPropertyValue("VPC.VpcName"));
        model.setRegionId(properties.getPropertyValue("VPC.RegionId"));
        model.setCidrBlock(properties.getPropertyValue("VPC.CidrBlock"));
        
        VPCOperator vpcOper = new VPCOperator(client);
        return vpcOper.create(model);
    }
    
    private static void destroyVPC(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        VPCOperator vpcOper = new VPCOperator(client);
        String regionId = properties.getPropertyValue("VPC.RegionId");
        String vpcName = properties.getPropertyValue("VPC.VpcName");
        vpcOper.destroy(regionId, vpcName);
    }
    
    private static SecurityGroup createSecurityGroup(PropertiesLoader properties, ClientAdapter client, Vpcs vpcs) throws ApiException{
        String securityGroupName = properties.getPropertyValue("SecurityGroup.SecurityGroupName");
        SecurityGroupOperator securityGroupOper = new SecurityGroupOperator(client);
        return securityGroupOper.create(vpcs.getRegionId(), vpcs.getVpcId(), securityGroupName);
    }
    
    private static void destroySecurityGroup(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        VPCOperator vpcOper = new VPCOperator(client);
        String regionId = properties.getPropertyValue("VPC.RegionId");
        String vpcName = properties.getPropertyValue("VPC.VpcName");
        Vpcs vpc = vpcOper.getByName(regionId, vpcName);
        if(vpc != null){
            SecurityGroupOperator securityGroupOper = new SecurityGroupOperator(client);
            String securityGroupName = properties.getPropertyValue("SecurityGroup.SecurityGroupName");
            securityGroupOper.destroy(vpc.getRegionId(), vpc.getVpcId(), securityGroupName);
        }
    }
    
    private static void createVSwitches(PropertiesLoader properties, ClientAdapter client, Vpcs vpcs) throws ApiException{
        //general instance parameters. 
        String regionId_tbc_general = properties.getPropertyValue("Instance.general.Copy.RegionId");
        
        // new operators
        InstanceOperator instanceOper = new InstanceOperator(client);
        VSwitchOperator vswitchOper = new VSwitchOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            
            // instance to be copy parameters. 
            String regionId_tbc = properties.getPropertyValue("Instance." + index + ".Copy.RegionId");
            String ip_tbc = properties.getPropertyValue("Instance." + index + ".Copy.IP");
            
            // if null then set general value
            regionId_tbc = regionId_tbc == null ? regionId_tbc_general : regionId_tbc;
            
            // get instance, system disk and data disks to be copy.
            Instance instance_tbc = instanceOper.get(regionId_tbc, id_tbc);
            
            // if null then set general value
            ip_tbc = ip_tbc == null ? String.valueOf(instance_tbc.getInnerIpAddress().get(0)) : ip_tbc;
            
            // create VSwitch. CidrBlock 10.x.0.0/16
            String x = ip_tbc.split("\\.")[1];
            String vSwitchName = properties.getPropertyValue("VSwitch.VSwitchName") + "_" + x;
            VSwitch vSwitch = vswitchOper.getByName(vpcs.getVpcId(), vSwitchName);
            if(vSwitch == null){
                VSwitch model = new VSwitch();
                model.setvSwitchName(vSwitchName);
                model.setZoneId(properties.getPropertyValue("VSwitch.ZoneId"));
                model.setCidrBlock("10." + x + ".0.0/16");
                model.setVpcId(vpcs.getVpcId());
                
                vSwitch = vswitchOper.create(model);
            }
        }
    }
    
    private static void destroyVSwitches(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        VPCOperator vpcOper = new VPCOperator(client);
        String regionId = properties.getPropertyValue("VPC.RegionId");
        String vpcName = properties.getPropertyValue("VPC.VpcName");
        Vpcs vpc = vpcOper.getByName(regionId, vpcName);
        if(vpc != null){
            VSwitchOperator vswitchOper = new VSwitchOperator(client);
            //destroy all switches under vpc
            List<String> vSwitchIds = vpc.getvSwitchIds();
            if(vSwitchIds != null){
                for(String vSwitchId: vSwitchIds){
                    vswitchOper.destroy(vSwitchId);
                }
            }
        }
    }
    
    private static void createAndAssociateEip(PropertiesLoader properties, ClientAdapter client, Vpcs vpcs) throws ApiException{
        //general instance parameters. 
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        
        // new operators
        InstanceOperator instanceOper = new InstanceOperator(client);
        EipAddressOperator eipOper = new EipAddressOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            String eipBand = properties.getPropertyValue("Instance." + index + ".EipBind");
            
            // if null then set general value
            regionId = regionId == null ? regionId_general : regionId;
            
            Instance instance = instanceOper.getByName(regionId, instanceName);
            
            // create and associate EIP
            if("true".equals(eipBand) 
                    && (instance.getEipAddress() == null 
                            || instance.getEipAddress().getAllocationId() == null
                            || instance.getEipAddress().getAllocationId().equals(""))){
                Eip model = new Eip();
                model.setRegionId(instance.getRegionId());
                model.setInstanceId(instance.getInstanceId());
                model.setBandwidth(properties.getPropertyValue("Eip.Bandwidth"));
                model.setInternetChargeType(properties.getPropertyValue("Eip.InternetChargeType"));
                
                eipOper.createAndAssociate(model);
            }
        }
    }
    
    private static void unassociateAndDesdroyEip(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        //general instance parameters. 
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String eip_bind = properties.getPropertyValue("Instance." + index + ".EipBind");
            if(eip_bind == null || eip_bind.trim().equals("true") == false){
                continue;
            }
            
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            
            //if null then set general value
            regionId = regionId == null ? regionId_general : regionId;
            
            InstanceOperator instanceOper = new InstanceOperator(client);
            
            Instance instance = instanceOper.getByName(regionId, instanceName);
            
            if(instance != null && instance.getEipAddress() != null){
                String allocationId = instance.getEipAddress().getAllocationId();
                EipAddressOperator eipOper = new EipAddressOperator(client);
                eipOper.unassociateAndDestroy(regionId, allocationId, instance.getInstanceId());
            }
        }
    }
    
    private static List<Instance> createInstances(PropertiesLoader properties, ClientAdapter client, 
            Vpcs vpcs, SecurityGroup securityGroup) throws ApiException{
        List<Instance> result = new ArrayList<Instance>();
        
        //general instance parameters. 
        String regionId_tbc_general = properties.getPropertyValue("Instance.general.Copy.RegionId");
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        String password_general = properties.getPropertyValue("Instance.general.Password");
        
        // new operators
        InstanceOperator instanceOper = new InstanceOperator(client);
        DiskOperator diskOper = new DiskOperator(client);
        SnapshotOperator snapshotOper = new SnapshotOperator(client);
        ImageOperator imageOper = new ImageOperator(client);
        VSwitchOperator vswitchOper = new VSwitchOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            
            //instance to be copy parameters. 
            String regionId_tbc = properties.getPropertyValue("Instance." + index + ".Copy.RegionId");
            String ip_tbc = properties.getPropertyValue("Instance." + index + ".Copy.IP");
            
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            String password = properties.getPropertyValue("Instance." + index + ".Password");
            
            // if null then set general value
            regionId_tbc = regionId_tbc == null ? regionId_tbc_general : regionId_tbc;
            regionId = regionId == null ? regionId_general : regionId;
            password = password == null ? password_general : password;
            
            // get instance, system disk and data disks to be copy.
            Instance instance_tbc = instanceOper.get(regionId_tbc, id_tbc);
            Disk sysDisk_tbc = diskOper.getInstanceSystemDisk(regionId_tbc, id_tbc);
            List<Disk> dataDisks_tbc = diskOper.getInstanceDataDisks(regionId_tbc, id_tbc);
            
            // get image
            String imageName = instanceName + "_image";
            Image image_sys = imageOper.getLatest(regionId_tbc, imageName);
            
            // system disk
            Disk model_disk_sys = new Disk();
            model_disk_sys.setCategory(sysDisk_tbc.getCategory());
            model_disk_sys.setDiskName(instanceName + "_disk_sys");
            
            // data disks
            List<Disk> model_disk_datas = new ArrayList<Disk>();
            if(dataDisks_tbc != null && dataDisks_tbc.size() != 0){
                int size = dataDisks_tbc.size();
                for(int sub_index = 0; sub_index < size; sub_index ++){
                    Disk disk_tbc = dataDisks_tbc.get(sub_index);
                    Snapshot snapshot_data = snapshotOper.getLatest(regionId_tbc, instance_tbc.getInstanceId(), disk_tbc.getDiskId());
                    Disk model_disk_data = new Disk();
                    model_disk_data.setCategory(disk_tbc.getCategory());
                    model_disk_data.setDiskName(instanceName + "_disk_data_" + sub_index);
                    model_disk_data.setDevice(disk_tbc.getDevice());
                    model_disk_data.setSourceSnapshotId(snapshot_data.getSnapshotId());
                    
                    model_disk_datas.add(model_disk_data);
                }
            }
            
            // if null then set general value
            ip_tbc = ip_tbc == null ? String.valueOf(instance_tbc.getInnerIpAddress().get(0)) : ip_tbc;
            
            // get VSwitch. CidrBlock 10.x.0.0/16
            String x = ip_tbc.split("\\.")[1];
            String vSwitchName = properties.getPropertyValue("VSwitch.VSwitchName") + "_" + x;
            VSwitch vSwitch = vswitchOper.getByName(vpcs.getVpcId(), vSwitchName);
            
            // create instance with image of the system disk and snapshots of the data disks. need VSwitchId and SecurityGroupId.
            Instance model_instance = new Instance();
            model_instance.setImageId(image_sys.getImageId());
            model_instance.setInstanceName(instanceName);
            model_instance.setRegionId(regionId);
            model_instance.setInstanceType(instance_tbc.getInstanceType());
            model_instance.setHostName(instance_tbc.getHostName());
            
            // create instance
            Instance instance = instanceOper.create(model_instance, vSwitch.getvSwitchId(), 
                    securityGroup.getSecurityGroupId(), ip_tbc, password, 
                    model_disk_sys, model_disk_datas);
            
            result.add(instance);
        }
        return result;
    }
    
    private static void destroyInstances(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        //general instance parameters. 
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        
        InstanceOperator instanceOper = new InstanceOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            
            // if null then set general value
            regionId = regionId == null ? regionId_general : regionId;
            
            //destroy instance with the system disk and the data disks together.
            instanceOper.destroy(regionId, instanceName);
        }
    }
    
    private static void startInstances(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        //general instance parameters. 
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        
        InstanceOperator instanceOper = new InstanceOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            
            // if null then set general value
            regionId = regionId == null ? regionId_general : regionId;
            
            if(instanceName != null && instanceName.trim().equals("") == false){
                instanceOper.start(regionId, instanceName);
            }
        }
    }
    
    private static void stopInstances(PropertiesLoader properties, ClientAdapter client) throws ApiException{
        //general instance parameters. 
        String regionId_general = properties.getPropertyValue("Instance.general.RegionId");
        
        InstanceOperator instanceOper = new InstanceOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            //instance model parameters. 
            String regionId = properties.getPropertyValue("Instance." + index + ".RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            
            // if null then set general value
            regionId = regionId == null ? regionId_general : regionId;
            
            if(instanceName != null && instanceName.trim().equals("") == false){
                instanceOper.stop(regionId, instanceName);
            }
        }
    }
    
    private static void createSnapshots(final PropertiesLoader properties, final ClientAdapter client, 
    		final boolean forceCreate, final boolean multiThread) throws ApiException{
        //general instance parameters. 
    	final String regionId_tbc_general = properties.getPropertyValue("Instance.general.Copy.RegionId");
        
        // new operators
    	final DiskOperator diskOper = new DiskOperator(client);
    	final SnapshotOperator snapshotOper = new SnapshotOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
        	final String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            
            final int tmp_index = index;
            Thread thread = new Thread(){
				@Override
				public void run() {
					try {
						//instance to be copy parameters. 
						String regionId_tbc = properties.getPropertyValue("Instance." + tmp_index + ".Copy.RegionId");
						
						//instance model parameters. 
						String instanceName = properties.getPropertyValue("Instance." + tmp_index + ".InstanceName");
						
						// if null then set general value
						regionId_tbc = regionId_tbc == null ? regionId_tbc_general : regionId_tbc;
						
						List<Disk> disks = new ArrayList<Disk>();
						disks.add(diskOper.getInstanceSystemDisk(regionId_tbc, id_tbc));
						disks.addAll(diskOper.getInstanceDataDisks(regionId_tbc, id_tbc));
						
						int size = disks.size();
						for(int sub_index = 0; sub_index < size; sub_index ++){
						    Disk disk_tbc = disks.get(sub_index);
						    
						    Snapshot snapshot = null;
						    
						    if(forceCreate == false){
						        snapshot = snapshotOper.getLatest(regionId_tbc, id_tbc, disk_tbc.getDiskId());
						    }
						    
						    if(snapshot == null){
						        Snapshot model_snapshot = new Snapshot();
						        model_snapshot.setSnapshotName(instanceName + "_snapshot_" + tmp_index + "_" +sub_index);
						        model_snapshot.setSourceDiskId(disk_tbc.getDiskId());
						        if(multiThread){
						        	snapshot = snapshotOper.create(model_snapshot, regionId_tbc, false);
					            }else{
					            	snapshot = snapshotOper.create(model_snapshot, regionId_tbc, true);
					            }
						    }
						}
					} catch (ApiException e) {
						e.printStackTrace();
					}
				}
            };
            
            if(multiThread){
            	thread.start();
            }else{
            	thread.run();
            }
        }
    }
    
    private static void createImages(PropertiesLoader properties, ClientAdapter client, boolean forceCreate) throws ApiException{
        //general instance parameters. 
        String regionId_tbc_general = properties.getPropertyValue("Instance.general.Copy.RegionId");
        
        // new operators
        DiskOperator diskOper = new DiskOperator(client);
        SnapshotOperator snapshotOper = new SnapshotOperator(client);
        ImageOperator imageOper = new ImageOperator(client);
        
        //try from 1 to 99
        for(int index = 1; index < instance_max_index; index ++){
            String id_tbc = properties.getPropertyValue("Instance." + index + ".Copy.ID");
            if(id_tbc == null || id_tbc.trim().equals("")){
                continue;
            }
            
            String regionId_tbc = properties.getPropertyValue("Instance." + index + ".Copy.RegionId");
            String instanceName = properties.getPropertyValue("Instance." + index + ".InstanceName");
            
            regionId_tbc = regionId_tbc == null ? regionId_tbc_general : regionId_tbc;
            
            String imageName = instanceName + "_image";
            
            Image image_sys = imageOper.getLatest(regionId_tbc, imageName);
            
            //if force create then destroy firstly.
            if(forceCreate == true && image_sys != null){
            	imageOper.destroy(regionId_tbc, image_sys.getImageId());
            	image_sys = null;
            }
            
            if(image_sys == null){
                Disk sysDisk_tbc = diskOper.getInstanceSystemDisk(regionId_tbc, id_tbc);
                Snapshot snapshot = snapshotOper.getLatest(regionId_tbc, id_tbc, sysDisk_tbc.getDiskId());
                image_sys = imageOper.create(regionId_tbc, snapshot.getSnapshotId(), imageName);
            }
        }
    }
}

package org.whb.build;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HelloBuildTest {

    @Test
    public void testAdd() {
        HelloBuild helloBuild = new HelloBuild();
        int result = helloBuild.add(1, 2);
        assertEquals(result, 3);
    }

}

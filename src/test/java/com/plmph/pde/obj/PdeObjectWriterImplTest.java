package com.plmph.pde.obj;

import com.plmph.pde.PdeFieldTypes;
import com.plmph.pde.util.TestConstants;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PdeObjectWriterImplTest {

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException {
        PdeObjectWriterImpl pdeObjectWriter = new PdeObjectWriterImpl(Pojo1.class);

        byte[] dest = new byte[1024];

        Pojo1 pojo = new Pojo1();

        int bytesWritten = pdeObjectWriter.writeKeysAndValues(dest, 0, pojo, 1);

        String str = new String(Arrays.copyOfRange(dest, 0, bytesWritten), StandardCharsets.US_ASCII);

        assertEquals(TestConstants.pojo1AsASCIIString, str);
        assertEquals(TestConstants.pojo1AsASCIIString.length(), bytesWritten);
    }
}

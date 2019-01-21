package test;

import java.io.File;

import javax.xml.bind.JAXBException;

public class TestMyOwnDisplay {

    public static void main(String[] arg) throws InterruptedException, JAXBException {
        testLoadXML();
    }

    /*
      _____
     /     \
     |     |
     |   [[1]]
     |     |
     |    -+- 1
     |     |
     |    ===============
     |     |     |     |
     |    [2]   [3]   [4]
    /|\    |\    |     |
     |     | \   |     |
     |     |  \  |     |
     |     |   \ |     |
     |     |   ==========
     |    -+-2     |
     |     |       |
     |     |      -+- 3
     \_____/
      
     */

    public static void testLoadXML() throws JAXBException, InterruptedException {
        File file = new File("C:\\Users\\Marmotte\\Git\\JGrafcet\\resources\\test\\testGrafcet.xml");
    }
}

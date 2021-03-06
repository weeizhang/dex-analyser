import java.io.*;

public class Main {

    public static void main(String[] args) {

        DexHeader dexHeader = new DexHeader();
        FileInputStream inputStream = null;
        byte buffer1[] = new byte[1];
        byte buffer4[] = new byte[4];

        try {
            inputStream = new FileInputStream("resources/test.dex");
            dexHeader.readHeader(inputStream);

            inputStream.skip(8);//跳过DataSize&DataOff

            StringTable stringTable = new StringTable();
            stringTable.createStringListRef(inputStream, dexHeader.getStringTable().getSize());

            TypeTable typeTable = new TypeTable();
            typeTable.createTypeStringRef(inputStream, dexHeader.getTypeTable().getSize());

            PrototypeTable prototypeTable = new PrototypeTable();
            prototypeTable.createPrototypeRefList(inputStream, dexHeader.getPrototypeTable().getSize());

            FieldTable fieldTable = new FieldTable();
            fieldTable.createFieldRefList(inputStream, dexHeader.getFieldTable().getSize());

            MethodTable methodTable = new MethodTable();
            methodTable.createMethodRefList(inputStream, dexHeader.getMethodTable().getSize());

            ClassTable classTable = new ClassTable();
            classTable.createClassRefList(inputStream, dexHeader.getClassTable().getSize());

            System.out.println("Dex文件中字符串个数为：" + dexHeader.getStringTable().getSize());
            System.out.println("字符串依次是：");
            for (int i = 0; i < dexHeader.getStringTable().getSize(); i++) {
                inputStream = new FileInputStream("resources/test.dex");
                inputStream.skip(stringTable.getStringRef(i));
                inputStream.read(buffer1);
                int size = buffer1[0];

                byte buffer[] = new byte[size];
                inputStream.read(buffer, 0, size);
                stringTable.addStringList(new String(buffer));
                System.out.println("     字符串" + (i + 1) + ":" + new String(buffer));
            }

            System.out.println("Dex文件中类型个数为：" + dexHeader.getTypeTable().getSize());
            System.out.println("类型依次是：");
            for (int i = 0; i < dexHeader.getTypeTable().getSize(); i++) {
                int ref = typeTable.getTypeStringRef(i);
                typeTable.addTypeList(stringTable.getStringList().get(ref));
                System.out.println("     类型" + (i + 1) + ":" + stringTable.getStringList().get(ref));
            }

            System.out.println("Dex文件中原型个数为：" + dexHeader.getPrototypeTable().getSize());
            System.out.println("原型依次是：");
            int prototypeSize = dexHeader.getPrototypeTable().getSize();
            for (int i = 0; i < prototypeSize; i++) {
                inputStream = new FileInputStream("resources/test.dex");
                prototypeTable.addPrototype(stringTable, typeTable, inputStream, prototypeTable.getPrototypeRefList().get(i));
                System.out.println("原型" + (i + 1) + ":" + prototypeTable.getPrototypeList().get(i).toString() + "\n");
            }

            System.out.println("Dex文件中字段个数为：" + dexHeader.getFieldTable().getSize());
            System.out.println("字段依次是：");
            int fieldSize = dexHeader.getFieldTable().getSize();
            for (int i = 0; i < fieldSize; i++) {
                fieldTable.addFieldList(stringTable, typeTable, fieldTable.getFieldRefList().get(i));
                System.out.println("字段" + (i + 1) + ":" + fieldTable.getFieldList().get(i).toString() + "\n");
            }

            System.out.println("Dex文件中方法个数为：" + dexHeader.getMethodTable().getSize());
            System.out.println("方法依次是：");
            int methodSize = dexHeader.getMethodTable().getSize();
            for (int i = 0; i < methodSize; i++) {
                methodTable.addMethodList(stringTable, typeTable, prototypeTable, methodTable.getMethodRefList().get(i));
                System.out.println("方法" + (i + 1) + ":" + methodTable.getMethodList().get(i).toString() + "\n");
            }

            System.out.println("Dex文件中类个数为：" + dexHeader.getClassTable().getSize());
            System.out.println("类依次是：");
            int classSize = dexHeader.getClassTable().getSize();
            for (int i = 0; i < classSize; i++) {
                inputStream = new FileInputStream("resources/test.dex");
                classTable.addClass(stringTable, typeTable, inputStream, classTable.getClassRefList().get(i));
                inputStream = new FileInputStream("resources/test.dex");
                inputStream.skip(classTable.getClassRefList().get(i).getClassDataOff());
                classTable.getClassList().get(i).createClassData(inputStream);
                System.out.println("类" + (i + 1) + ":" + classTable.getClassList().get(i).toString() + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

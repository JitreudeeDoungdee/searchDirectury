import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.*;
import java.util.regex.*;
public class Search {
    private static  String FDN;
    private static String LDirec;
    private static boolean notFounded = true;
    private static String word;
    private static String[] listDirec;
    private static ArrayList<String> AL = new ArrayList<>();
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {
        Search s = new Search();
        String path = "/home/jitreudee/Desktop";
        String SearchWord = "pde";
        /*String path = args[0];
        String SearchWord = args[1];*/
        System.out.println("Word : " + SearchWord);

        s.TextSearch(path,SearchWord);
       // s.showDirectory(path,SearchWord);
        //s.SaveFileXML();
    }

    void TextSearch(String path,String SearchWord) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        Scanner s = new Scanner(new FileReader("Backup.txt"));
        while (s.hasNext()) {
            result.add(s.nextLine());
        }
        if(result.size()!=0){
            for (String value : result) {
                LDirec = value;
                String[] readBuffer = LDirec.split("/");
                if (IsMatch(readBuffer[readBuffer.length - 1], SearchWord)) {
                    //System.out.println("Founded From Backup.");
                    OP();
                }
            }
            if(notFounded){
                System.out.println("Not Founded on Backup...");
                showDirectory(path,SearchWord);
            }
        }
        else{
            showDirectory(path,SearchWord);
        }

        if(notFounded){
            System.out.println("Don't have file " + SearchWord);
        }
        System.out.println("--------------");
    }
    int totalFolder=0;
    int totalFile=0;
    long getFileSize(File folder) {
        totalFolder++;
        //System.out.println("Folder: " + folder.getName());
        long foldersize = 0;

        File[] filelist = folder.listFiles();
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                foldersize += getFileSize(filelist[i]);
            } else {
                totalFile++;
                foldersize += filelist[i].length();
            }
        }
        return foldersize;
    }
    public int getTotalFolder() {
        return totalFolder;
    }
    public int getTotalFile() {
        return totalFile;
    }
    void showDirectory(String Path, String SearchWord) throws IOException {
        File file = new File(Path);
        if(file.isDirectory()){
            String[] names = file.list();
            long size = getFileSize(file);
            FDN = file.getName();
            //AL.add(file.getName());
            assert names != null;
            for (String name : names) {
                word = word + Path + "/" + name + ",";
                listDirec = word.split(",");
                SaveFile(listDirec);
                AL.add(name);
                LDirec = Path + "/" + name;
                //AL.add(LDirec);
                if (IsMatch(name, SearchWord)) {
                    OP();
                }
                showDirectory(Path + "/" + name, SearchWord);
            }
        }
    }

    boolean IsMatch(String word, String pattern){
        char[] Buf = pattern.toCharArray();
        Pattern pt = Pattern.compile("[" + pattern + "]",Pattern.CASE_INSENSITIVE);
        Pattern pt1 = Pattern.compile(Buf[0]+"*"+Buf[Buf.length-1],Pattern.CASE_INSENSITIVE);
        Matcher mc = pt.matcher(word);
        Matcher mc1 = pt1.matcher(word);
        return mc.find() || mc1.find();
    }

    void OP(){
        System.out.println("Directory : " + LDirec);
        notFounded = false;
    }
    void SaveFile(String[] arr) throws IOException {
        FileWriter writer = new FileWriter("Backup.txt");
        for(String str: arr) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }
    void SaveFileXML() throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        try {
            Element rootElement = doc.createElement("Folder");
            rootElement.appendChild(doc.createTextNode(FDN));
            doc.appendChild(rootElement);
            for(String i : AL) {
                Element FD = doc.createElement("File");
                FD.appendChild(doc.createTextNode(i));
                rootElement.appendChild(FD);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("Backup.xml"));
                transformer.transform(source, result);
            }
            System.out.println("File xml saved!");
        } catch (TransformerException pce) {
            pce.printStackTrace();
        }
    }
}
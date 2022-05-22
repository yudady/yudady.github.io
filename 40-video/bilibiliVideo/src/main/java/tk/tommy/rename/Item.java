package tk.tommy.rename;

import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

public class Item {

    public String flvPrefixName;
    public String eachFolder;
    public String flvName;
    public Integer index;
    public List<Path> blvFolders;


    public Item(String flvPrefixName, String eachFolder, String flvName, Integer index) {
        this.flvPrefixName = flvPrefixName;
        this.eachFolder = eachFolder;
        this.flvName = flvName;
        this.index = index;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Item.class.getSimpleName() + "[", "]")
            .add("targetName='" + flvPrefixName + "'")
            .add("eachFolder='" + eachFolder + "'")
            .add("flvName='" + flvName + "'")
            .add("index=" + index)
            .toString();
    }
}

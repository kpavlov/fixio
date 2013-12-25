package fixio.fixprotocol;


import java.util.ArrayList;
import java.util.List;

public class GroupField extends FixMessageFragment {

    private final List<Group> groups = new ArrayList<>();

    protected GroupField(int tagNum) {
        super(tagNum);
    }

    public void add(Group group) {
        groups.add(group);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public List<Group> getGroups() {
        return groups;
    }
}

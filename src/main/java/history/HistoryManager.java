package history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryManager {

    private final List<String> entries = new ArrayList<>();

    public void add(String line) {
        if (entries.isEmpty() || !entries.get(entries.size() - 1).equals(line)) {
            entries.add(line);
        }
    }

    public List<String> getEntries() {
        return Collections.unmodifiableList(entries);
    }

}

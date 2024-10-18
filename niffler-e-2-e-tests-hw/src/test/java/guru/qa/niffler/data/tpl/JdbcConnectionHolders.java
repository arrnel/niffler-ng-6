package guru.qa.niffler.data.tpl;

import java.util.ArrayList;
import java.util.List;

public class JdbcConnectionHolders implements AutoCloseable {

    public JdbcConnectionHolders(List<JdbcConnectionHolder> holders) {
        this.holders.addAll(holders);
    }

    private final List<JdbcConnectionHolder> holders = new ArrayList<>();

    @Override
    public void close() {
        holders.forEach(JdbcConnectionHolder::close);
    }

}

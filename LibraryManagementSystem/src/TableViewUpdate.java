import book.Book;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import java.util.concurrent.*;


public class TableViewUpdate extends MainViewController implements Runnable {
    private ScheduledExecutorService executorService;
    TableViewUpdate() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
    }
    public void run(){
        Platform.runLater(() -> {
                bookTableView.setItems(observableBooks);
                bookTableView.refresh();
            });
        }
    }





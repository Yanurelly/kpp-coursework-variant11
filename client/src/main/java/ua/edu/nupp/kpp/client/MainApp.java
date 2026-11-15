package ua.edu.nupp.kpp.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class MainApp extends Application {
    private static final Logger LOG = Logger.getLogger(MainApp.class.getName());
    private final HttpWarehouseClient api = new HttpWarehouseClient();
    private final TableView<Product> products = new TableView<>();
    private final TableView<Supplier> suppliers = new TableView<>();
    private final TableView<Inventory> inventories = new TableView<>();
    private final TableView<ReplenishmentOrder> orders = new TableView<>();

    @Override
    public void start(Stage stage) {
        TabPane tabs = new TabPane(
                new Tab("Товари", productPane()),
                new Tab("Постачальники", supplierPane()),
                new Tab("Запаси", inventoryPane()),
                new Tab("Замовлення на поповнення", orderPane())
        );
        tabs.getTabs().forEach(t -> t.setClosable(false));
        stage.setScene(new Scene(tabs, 1150, 650));
        stage.setTitle("Управління складськими запасами (Клієнт)");
        stage.show();
        refreshAll();
    }

    private Pane productPane() {
        products.getColumns().setAll(
                col("ID", "id"),
                col("Артикул (SKU)", "sku"),
                col("Назва", "name"),
                col("Категорія", "category")
        );
        TextField sku = new TextField();
        TextField name = new TextField();
        TextField category = new TextField();
        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addProduct(new Product(null, sku.getText(), name.getText(), category.getText()));
            refreshProducts();
        }));
        update.setOnAction(e -> safe(() -> {
            Product p = selected(products);
            p.setSku(sku.getText());
            p.setName(name.getText());
            p.setCategory(category.getText());
            api.updateProduct(p);
            refreshProducts();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteProduct(selected(products).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshProducts());

        products.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            if (p != null) {
                sku.setText(p.getSku());
                name.setText(p.getName());
                category.setText(p.getCategory());
            }
        });

        VBox form = new VBox(8,
                new Label("Артикул (SKU)"), sku,
                new Label("Назва"), name,
                new Label("Категорія"), category,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(products, form);
    }

    private Pane supplierPane() {
        suppliers.getColumns().setAll(
                col("ID", "id"),
                col("Назва", "name"),
                col("Контакти", "contactInfo"),
                col("Ризик затримки (F3)", "delayRiskScore")
        );
        TextField name = new TextField();
        TextField contactInfo = new TextField();
        TextField delayRiskScore = new TextField();
        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addSupplier(new Supplier(null, name.getText(), contactInfo.getText(), Double.parseDouble(delayRiskScore.getText())));
            refreshSuppliers();
        }));
        update.setOnAction(e -> safe(() -> {
            Supplier s = selected(suppliers);
            s.setName(name.getText());
            s.setContactInfo(contactInfo.getText());
            s.setDelayRiskScore(Double.parseDouble(delayRiskScore.getText()));
            api.updateSupplier(s);
            refreshSuppliers();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteSupplier(selected(suppliers).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshSuppliers());

        suppliers.getSelectionModel().selectedItemProperty().addListener((o, a, s) -> {
            if (s != null) {
                name.setText(s.getName());
                contactInfo.setText(s.getContactInfo());
                delayRiskScore.setText(String.valueOf(s.getDelayRiskScore()));
            }
        });

        VBox form = new VBox(8,
                new Label("Назва"), name,
                new Label("Контактна інформація"), contactInfo,
                new Label("Ризик затримки (0..100)"), delayRiskScore,
                new HBox(8, add, update, delete, refresh)
        );
        return layout(suppliers, form);
    }

    private Pane inventoryPane() {
        inventories.getColumns().setAll(
                col("ID", "id"),
                col("ID товару", "productId"),
                col("Поточний запас", "currentStock"),
                col("Мін. запас", "minRequiredStock"),
                col("Інтенсивність (F2)", "consumptionIntensity")
        );
        TextField productId = new TextField();
        TextField currentStock = new TextField();
        TextField minRequiredStock = new TextField();
        TextField consumptionIntensity = new TextField();
        Button add = new Button("Додати");
        Button update = new Button("Змінити");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");
        Button importBtn = new Button("Імпорт CSV");

        add.setOnAction(e -> safe(() -> {
            api.addInventory(readInventory(null, productId, currentStock, minRequiredStock, consumptionIntensity));
            refreshInventories();
        }));
        update.setOnAction(e -> safe(() -> {
            Inventory inv = selected(inventories);
            api.updateInventory(readInventory(inv.getId(), productId, currentStock, minRequiredStock, consumptionIntensity));
            refreshInventories();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteInventory(selected(inventories).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshInventories());
        importBtn.setOnAction(e -> safe(this::importCsv));

        inventories.getSelectionModel().selectedItemProperty().addListener((o, a, inv) -> {
            if (inv != null) {
                productId.setText(String.valueOf(inv.getProductId()));
                currentStock.setText(String.valueOf(inv.getCurrentStock()));
                minRequiredStock.setText(String.valueOf(inv.getMinRequiredStock()));
                consumptionIntensity.setText(String.valueOf(inv.getConsumptionIntensity()));
            }
        });

        FlowPane buttonsPane = new FlowPane(6, 6, add, update, delete, refresh, importBtn);

        VBox form = new VBox(8,
                new Label("ID товару"), productId,
                new Label("Поточний запас"), currentStock,
                new Label("Мінімальний запас"), minRequiredStock,
                new Label("Інтенсивність споживання (0..100)"), consumptionIntensity,
                buttonsPane
        );
        return layout(inventories, form);
    }

    private void importCsv() {
        try {
            Path path = Files.exists(Path.of("sample-data", "inventory.csv"))
                    ? Path.of("sample-data", "inventory.csv")
                    : Path.of("client", "sample-data", "inventory.csv");
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("product")) {
                    continue;
                }
                String[] parts = trimmed.split("[;,]");
                Inventory inv = new Inventory(
                        null,
                        Long.parseLong(parts[0].trim()),
                        Double.parseDouble(parts[1].trim()),
                        Double.parseDouble(parts[2].trim()),
                        Double.parseDouble(parts[3].trim())
                );
                api.addInventory(inv);
            }
            refreshInventories();
        } catch (Exception e) {
            throw new IllegalArgumentException("Помилка читання CSV: " + e.getMessage());
        }
    }

    private Pane orderPane() {
        orders.getColumns().setAll(
                col("ID", "id"),
                col("ID запасу", "inventoryId"),
                col("ID постачальника", "supplierId"),
                col("Стан", "state"),
                col("F1", "f1"),
                col("F2", "f2"),
                col("F3", "f3"),
                col("I (Ризик)", "riskIndex")
        );
        TextField inventoryId = new TextField();
        TextField supplierId = new TextField();
        ComboBox<OrderState> state = new ComboBox<>(FXCollections.observableArrayList(OrderState.values()));
        Button add = new Button("Створити");
        Button update = new Button("Перерахувати");
        Button change = new Button("Змінити стан");
        Button delete = new Button("Видалити");
        Button refresh = new Button("Оновити");

        add.setOnAction(e -> safe(() -> {
            api.addOrder(Long.parseLong(inventoryId.getText()), Long.parseLong(supplierId.getText()));
            refreshOrders();
        }));
        update.setOnAction(e -> safe(() -> {
            api.updateOrder(selected(orders).getId(), Long.parseLong(inventoryId.getText()), Long.parseLong(supplierId.getText()));
            refreshOrders();
        }));
        change.setOnAction(e -> safe(() -> {
            api.changeOrderState(selected(orders).getId(), state.getValue());
            refreshOrders();
        }));
        delete.setOnAction(e -> safe(() -> {
            api.deleteOrder(selected(orders).getId());
            refreshAll();
        }));
        refresh.setOnAction(e -> refreshOrders());

        orders.getSelectionModel().selectedItemProperty().addListener((o, a, ord) -> {
            if (ord != null) {
                inventoryId.setText(String.valueOf(ord.getInventoryId()));
                supplierId.setText(String.valueOf(ord.getSupplierId()));
                state.setValue(ord.getState());
            }
        });

        FlowPane buttonsPane = new FlowPane(6, 6, add, update, change, delete, refresh);

        VBox form = new VBox(8,
                new Label("ID запасу"), inventoryId,
                new Label("ID постачальника"), supplierId,
                new Label("Новий стан"), state,
                buttonsPane
        );
        return layout(orders, form);
    }

    private Inventory readInventory(Long id, TextField productId, TextField currentStock, TextField minRequiredStock, TextField consumptionIntensity) {
        return new Inventory(
                id,
                Long.parseLong(productId.getText()),
                Double.parseDouble(currentStock.getText()),
                Double.parseDouble(minRequiredStock.getText()),
                Double.parseDouble(consumptionIntensity.getText())
        );
    }

    private void refreshAll() {
        refreshProducts();
        refreshSuppliers();
        refreshInventories();
        refreshOrders();
    }

    private void refreshProducts() {
        safe(() -> products.setItems(FXCollections.observableArrayList(api.getProducts())));
    }

    private void refreshSuppliers() {
        safe(() -> suppliers.setItems(FXCollections.observableArrayList(api.getSuppliers())));
    }

    private void refreshInventories() {
        safe(() -> inventories.setItems(FXCollections.observableArrayList(api.getInventories())));
    }

    private void refreshOrders() {
        safe(() -> orders.setItems(FXCollections.observableArrayList(api.getOrders())));
    }

    private <T> T selected(TableView<T> table) {
        T v = table.getSelectionModel().getSelectedItem();
        if (v == null) {
            throw new IllegalArgumentException("Виберіть запис");
        }
        return v;
    }

    private void safe(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LOG.warning(e.getMessage());
            showError(e);
        }
    }

    private void showError(Throwable e) {
        Alert d = new Alert(Alert.AlertType.ERROR, e == null ? "Помилка" : e.getMessage(), ButtonType.OK);
        d.showAndWait();
    }

    private Pane layout(TableView<?> table, Pane form) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        SplitPane p = new SplitPane(table, form);
        p.setDividerPositions(0.55);
        BorderPane root = new BorderPane(p);
        root.setPadding(new Insets(10));
        return root;
    }

    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        return c;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
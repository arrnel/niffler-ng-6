package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DeleteSpendingExtension implements AfterEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DeleteSpendingExtension.class);

    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
//        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Spending.class)
//                .ifPresent(
//                    spendApiClient.deleteSpends(
//                List.of((SpendJson) context.getStore(CreateSpendingExtension.NAMESPACE).getOrComputeIfAbsent(context.getUniqueId(),null))
//                    )
//                );
    }

}

package practices.handroll.stub

//
interface DataProvider {
    fun getData(id: Int): String?
}

class DataProcessor(private val dataProvider: DataProvider) {
    fun process(id: Int, fallback: String = "Default Data"): String {
        return "Processed: ${dataProvider.getData(id) ?: fallback}"
    }
}

package practices.handroll.stub

//
class StubDataProvider(private val responses: Iterator<String?>) : DataProvider {
    override fun getData(id: Int) = responses.next()
}

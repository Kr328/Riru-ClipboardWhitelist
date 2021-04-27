open class RiruExtension {
    var id: String = ""
    var name: String = ""
    var minApi: Int = -1
    var minApiName: String = ""
    var description: String = ""
    var author: String = ""

    val riruId: String
        get() = id.removePrefix("riru_")
}
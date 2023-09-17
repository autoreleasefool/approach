import StringsLibrary

public struct Tip: Hashable {
	public let id: String
	public let title: String
	public let message: String?

	public init(id: String? = nil, title: String, message: String? = nil) {
		self.id = id ?? title
		self.title = title
		self.message = message
	}
}

import StringsLibrary

public struct ResourceListError: Equatable {
	public let title: String
	public let message: String?
	public let action: String

	public init(title: String, message: String? = nil, action: String) {
		self.title = title
		self.message = message
		self.action = action
	}

	public static let failedToLoad = Self(
		title: Strings.Error.Generic.title,
		message: Strings.Error.loadingFailed,
		action: Strings.Action.reload
	)

	public static let failedToDelete = Self(
		title: Strings.Error.Generic.title,
		action: Strings.Action.reload
	)
}

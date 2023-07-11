extension Analytics.Widget {
	public struct Deleted: TrackableEvent {
		public let name = "Widget.Deleted"

		public let context: String

		public var payload: [String: String]? {
			[
				"Context": context,
			]
		}

		public init(context: String) {
			self.context = context
		}
	}
}

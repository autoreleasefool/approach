extension Analytics.Widget {
	public struct LayoutUpdated: TrackableEvent {
		public let name = "Widget.LayoutUpdated"

		public let context: String
		public let numberOfWidgets: Int

		public var payload: [String: String]? {
			[
				"Context": context,
				"NumberOfWidgets": String(numberOfWidgets),
			]
		}

		public init(context: String, numberOfWidgets: Int) {
			self.context = context
			self.numberOfWidgets = numberOfWidgets
		}
	}
}

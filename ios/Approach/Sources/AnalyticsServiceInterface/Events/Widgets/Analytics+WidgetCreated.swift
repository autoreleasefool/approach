extension Analytics.Widget {
	public struct Created: TrackableEvent {
		public let name = "Widget.Created"

		public let context: String
		public let source: String
		public let statistic: String
		public let timeline: String

		public var payload: [String: String]? {
			[
				"Context": context,
				"Source": source,
				"Statistic": statistic,
				"Timeline": timeline,
			]
		}

		public init(
			context: String,
			source: String,
			statistic: String,
			timeline: String
		) {
			self.context = context
			self.source = source
			self.statistic = statistic
			self.timeline = timeline
		}
	}
}

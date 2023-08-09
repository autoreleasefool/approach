import StringsLibrary

public enum Tip: String {

	// MARK: - Statistics
	case statisticsOverview
	case statisticsDetails

	public var title: String {
		switch self {
		case .statisticsOverview:
			return Strings.Statistics.Overview.GetAnOverviewHint.title
		case .statisticsDetails:
			return Strings.Statistics.Overview.ViewMoreDetailsHint.title
		}
	}

	public var message: String {
		switch self {
		case .statisticsOverview:
			return Strings.Statistics.Overview.GetAnOverviewHint.message
		case .statisticsDetails:
			return Strings.Statistics.Overview.ViewMoreDetailsHint.message
		}
	}
}

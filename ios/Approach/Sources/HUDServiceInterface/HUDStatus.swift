public enum HUDStatus: Sendable {
	case show(Set<HUDStyle>)
	case hide

	public var isShowing: Bool {
		switch self {
		case .show: true
		case .hide: false
		}
	}
}

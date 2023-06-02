extension GamesEditor.State {
	public enum Sheet: Equatable {
		case gameDetails
		case ballPicker
		case settings
		case opponentPicker
		case gearPicker

		static let `default`: Self = .gameDetails
	}

	public enum SheetState: Equatable {
		case presenting(Sheet)
		case transitioning(to: Sheet)

		mutating func handle(isPresented: Bool, for sheet: Sheet) {
			switch self {
			case .presenting:
				if isPresented {
					self.transition(to: sheet)
				} else {
					self.transition(to: .default)
				}
			case .transitioning:
				break
			}
		}

		mutating func transition(to: Sheet) {
			switch self {
			case .presenting(to):
				break
			case .presenting, .transitioning:
				self = .transitioning(to: to)
			}
		}

		mutating func hide(_ sheet: Sheet) {
			switch self {
			case .presenting(sheet), .transitioning(to: sheet):
				self.transition(to: .default)
			case .presenting, .transitioning:
				break
			}
		}

		mutating func finishTransition() {
			switch self {
			case let .transitioning(to):
				self = .presenting(to)
			case .presenting:
				break
			}
		}
	}
}

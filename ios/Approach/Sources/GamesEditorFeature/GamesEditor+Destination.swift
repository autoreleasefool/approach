import ComposableArchitecture
import EquatableLibrary
import ModelsLibrary
import ResourcePickerLibrary

extension GamesEditor {
	public struct Destination: Reducer {
		public enum State: Equatable {
			case gameDetails(GameDetails.State)
			case settings(GamesSettings.State)
			case opponentPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.State)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case ballPicker(ResourcePicker<Gear.Summary, Bowler.ID>.State)
		}

		public enum Action: Equatable {
			case gameDetails(GameDetails.Action)
			case settings(GamesSettings.Action)
			case opponentPicker(ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>.Action)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case ballPicker(ResourcePicker<Gear.Summary, Bowler.ID>.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.gear) var gear

		public var body: some ReducerOf<Self> {
			Scope(state: /State.gameDetails, action: /Action.gameDetails) {
				GameDetails()
			}
			Scope(state: /State.settings, action: /Action.settings) {
				GamesSettings()
			}
			Scope(state: /State.opponentPicker, action: /Action.opponentPicker) {
				ResourcePicker { _ in bowlers.opponents(ordered: .byName) }
			}
			Scope(state: /State.gearPicker, action: /Action.gearPicker) {
				ResourcePicker { _ in gear.list(ordered: .byName) }
			}
			Scope(state: /State.ballPicker, action: /Action.ballPicker) {
				ResourcePicker { bowler in gear.list(ownedBy: bowler, ofKind: .bowlingBall, ordered: .byName) }
			}
		}
	}
}

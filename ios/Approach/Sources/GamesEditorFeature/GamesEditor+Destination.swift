import BowlersRepositoryInterface
import ComposableArchitecture
import EquatableLibrary
import GearRepositoryInterface
import LanesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import SharingFeature

extension GamesEditor {
	public struct Destination: Reducer {
		public enum State: Equatable {
			case gameDetails(GameDetails.State)
			case settings(GamesSettings.State)
			case opponentPicker(ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.State)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.State)
			case sharing(Sharing.State)
		}

		public enum Action: Equatable {
			case gameDetails(GameDetails.Action)
			case settings(GamesSettings.Action)
			case opponentPicker(ResourcePicker<Bowler.Opponent, AlwaysEqual<Void>>.Action)
			case gearPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.Action)
			case sharing(Sharing.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.gear) var gear
		@Dependency(\.lanes) var lanes

		public var body: some ReducerOf<Self> {
			Scope(state: /State.gameDetails, action: /Action.gameDetails) {
				GameDetails()
			}
			Scope(state: /State.settings, action: /Action.settings) {
				GamesSettings()
			}
			Scope(state: /State.opponentPicker, action: /Action.opponentPicker) {
				ResourcePicker { _ in bowlers.opponents(ordering: .byName) }
			}
			Scope(state: /State.gearPicker, action: /Action.gearPicker) {
				ResourcePicker { _ in gear.list(ordered: .byName) }
			}
			Scope(state: /State.ballPicker, action: /Action.ballPicker) {
				ResourcePicker { _ in gear.list(ofKind: .bowlingBall, ordered: .byName) }
			}
			Scope(state: /State.lanePicker, action: /Action.lanePicker) {
				ResourcePicker { alley in lanes.list(alley) }
			}
			Scope(state: /State.sharing, action: /Action.sharing) {
				Sharing()
			}
		}
	}
}

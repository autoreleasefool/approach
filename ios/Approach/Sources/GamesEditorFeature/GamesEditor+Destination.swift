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
	public struct SheetsDestination: Reducer {
		public enum State: Equatable {
			case settings(GamesSettings.State)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.State)
			case sharing(Sharing.State)
		}

		public enum Action: Equatable {
			case settings(GamesSettings.Action)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case lanePicker(ResourcePicker<Lane.Summary, Alley.ID>.Action)
			case sharing(Sharing.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.gear) var gear
		@Dependency(\.lanes) var lanes

		public var body: some ReducerOf<Self> {
			Scope(state: /State.settings, action: /Action.settings) {
				GamesSettings()
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

	public struct Destination: Reducer {
		public enum State: Equatable {
			case gameDetails(GameDetails.State)
			case duplicateLanesAlert(AlertState<AlertAction>)
			case sheets(SheetsDestination.State)
		}

		public enum Action: Equatable {
			case gameDetails(GameDetails.Action)
			case duplicateLanesAlert(AlertAction)
			case sheets(SheetsDestination.Action)
		}

		public enum AlertAction: Equatable {
			case confirmDuplicateLanes
			case didDismiss
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.gameDetails, action: /Action.gameDetails) {
				GameDetails()
			}
			Scope(state: /State.sheets, action: /Action.sheets) {
				SheetsDestination()
			}
		}
	}
}

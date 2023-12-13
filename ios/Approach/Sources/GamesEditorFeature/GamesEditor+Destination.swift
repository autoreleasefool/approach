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
	@Reducer
	public struct SheetsDestination: Reducer {
		public enum State: Equatable {
			case settings(GamesSettings.State)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.State)
			case sharing(Sharing.State)
		}

		public enum Action {
			case settings(GamesSettings.Action)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
			case sharing(Sharing.Action)
		}

		@Dependency(\.bowlers) var bowlers
		@Dependency(\.gear) var gear

		public var body: some ReducerOf<Self> {
			Scope(state: \.settings, action: \.settings) {
				GamesSettings()
			}
			Scope(state: \.ballPicker, action: \.ballPicker) {
				ResourcePicker { _ in gear.list(ofKind: .bowlingBall, ordered: .byName) }
			}
			Scope(state: \.sharing, action: \.sharing) {
				Sharing()
			}
		}
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case gameDetails(GameDetails.State)
			case duplicateLanesAlert(AlertState<AlertAction>)
			case sheets(SheetsDestination.State)
		}

		public enum Action {
			case gameDetails(GameDetails.Action)
			case duplicateLanesAlert(AlertAction)
			case sheets(SheetsDestination.Action)
		}

		public enum AlertAction: Equatable {
			case confirmDuplicateLanes
			case didDismiss
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.gameDetails, action: \.gameDetails) {
				GameDetails()
			}
			Scope(state: \.sheets, action: \.sheets) {
				SheetsDestination()
			}
		}
	}
}

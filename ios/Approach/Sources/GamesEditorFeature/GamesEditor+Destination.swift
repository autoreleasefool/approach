import BowlersRepositoryInterface
import ComposableArchitecture
import EquatablePackageLibrary
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
		}

		public enum Action {
			case settings(GamesSettings.Action)
			case ballPicker(ResourcePicker<Gear.Summary, AlwaysEqual<Void>>.Action)
		}

		@Dependency(BowlersRepository.self) var bowlers
		@Dependency(GearRepository.self) var gear

		public var body: some ReducerOf<Self> {
			Scope(state: \.settings, action: \.settings) {
				GamesSettings()
			}
			Scope(state: \.ballPicker, action: \.ballPicker) {
				ResourcePicker { _ in gear.list(ofKind: .bowlingBall, ordered: .byName) }
			}
		}
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case gameDetails(GameDetails.State)
			case duplicateLanesAlert(AlertState<DuplicateLanesAlertAction>)
			case lockedAlert(AlertState<LockedAlertAction>)
			case sheets(SheetsDestination.State)
		}

		public enum Action {
			case gameDetails(GameDetails.Action)
			case duplicateLanesAlert(DuplicateLanesAlertAction)
			case lockedAlert(LockedAlertAction)
			case sheets(SheetsDestination.Action)
		}

		public enum DuplicateLanesAlertAction: Equatable {
			case confirmDuplicateLanes
			case didTapDismissButton
		}

		public enum LockedAlertAction: Equatable {
			case didTapDismissButton
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

extension GamesEditor.State {
	mutating func presentLockedAlert() -> Effect<GamesEditor.Action> {
		willShowLockAlert = true
		destination = nil
		return .none
	}
}

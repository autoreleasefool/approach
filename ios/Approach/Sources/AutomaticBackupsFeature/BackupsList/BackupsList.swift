import AnalyticsServiceInterface
import ComposableArchitecture
import DateTimeLibrary
import FeatureActionLibrary
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@Reducer
public struct BackupsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var errorDescription: String?
		public var daysSinceLastBackup: DaysSince
		public var daysSinceLastExport: DaysSince

		public init() {
			@Dependency(\.date) var date
			@Dependency(ExportService.self) var export
			@Dependency(BackupsService.self) var backups
			daysSinceLastBackup = backups.lastSuccessfulBackupDate()?.daysSince(date()) ?? .never
			daysSinceLastExport = export.lastExportDate()?.daysSince(date()) ?? .never
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didFirstAppear
			case didTapManualSyncButton
		}

		@CasePathable public enum Internal { case doNothing }
		@CasePathable public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .none

				case .didTapManualSyncButton:
					// TODO: force manual sync
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .doNothing:
					return .none
				}

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didFirstAppear):
				return Analytics.Backups.ListViewed()
			default:
				return .none
			}
		}
	}
}

@ViewAction(for: BackupsList.self)
public struct BackupsListView: View {
	public let store: StoreOf<BackupsList>

	public init(store: StoreOf<BackupsList>) {
		self.store = store
	}

	public var body: some View {
		Text("BackupsList")
			.onFirstAppear { send(.didFirstAppear) }
	}
}

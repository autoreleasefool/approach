import AnalyticsServiceInterface
import ComposableArchitecture
import DateTimeLibrary
import ErrorsFeature
import FeatureActionLibrary
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct BackupsList: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var errorDescription: String?
		public var daysSinceLastBackup: DaysSince
		public var backups: IdentifiedArrayOf<BackupFile> = []
		public var isAutomaticBackupsEnabled: Bool

		public var errors: Errors<ErrorID>.State = .init()

		public init() {
			@Dependency(\.date) var date
			@Dependency(BackupsService.self) var backups
			daysSinceLastBackup = backups.lastSuccessfulBackupDate()?.daysSince(date()) ?? .never

			@Dependency(\.preferences) var preferences
			self.isAutomaticBackupsEnabled = preferences.bool(forKey: .dataICloudBackupEnabled) ?? true
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didFirstAppear
			case didStartTask
			case didTapManualSyncButton
			case didTapBackupFile(BackupFile.ID)
			case didSwipe(BackupFile.ID, SwipeAction)
		}

		@CasePathable public enum Internal {
			case didLoadRecentBackups(Result<[BackupFile], Error>)

			case errors(Errors<ErrorID>.Action)
		}
		@CasePathable public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum SwipeAction {
		case delete
	}

	public enum ErrorID: Hashable {
		case failedToLoadBackups
	}

	public init() {}

	@Dependency(BackupsService.self) var backups
	@Dependency(\.date) var date
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .none

				case .didStartTask:
					return .run { send in
						await send(.internal(.didLoadRecentBackups(Result {
							try await self.backups.listBackups()
						})))
					}

				case let .didTapBackupFile(id):
					guard let file = state.backups[id: id] else { return .none }
					// TODO: request overwrite
					return .none

				case let .didSwipe(id, .delete):
					guard let file = state.backups[id: id] else { return .none }
					// TODO: request delete
					return .none

				case .didTapManualSyncButton:
					// TODO: force manual sync
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadRecentBackups(.success(backups)):
					state.backups = IdentifiedArrayOf(uniqueElements: backups)
					return .none

				case let .didLoadRecentBackups(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadBackups, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .binding(\.isAutomaticBackupsEnabled):
				return .run { [updatedValue = state.isAutomaticBackupsEnabled] _ in
					preferences.setBool(forKey: .dataICloudBackupEnabled, to: updatedValue)
				}
				.cancellable(id: PreferenceKey.dataICloudBackupEnabled, cancelInFlight: true)

			case .delegate, .binding:
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

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadRecentBackups(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: BackupsList.self)
public struct BackupsListView: View {
	@Bindable public var store: StoreOf<BackupsList>

	public init(store: StoreOf<BackupsList>) {
		self.store = store
	}

	public var body: some View {
		List {
			Section {
				Text(Strings.Backups.List.WhyEnable.title)
					.font(.headline)
					.frame(maxWidth: .infinity, alignment: .leading)

				Text(Strings.Backups.List.WhyEnable.description)
					.frame(maxWidth: .infinity, alignment: .leading)
			}
			.listRowSeparator(.hidden)

			Section {
				Toggle(
					Strings.Backups.List.EnableAutomaticBackups.title,
					isOn: $store.isAutomaticBackupsEnabled
				)
			} footer: {
				Text(Strings.Backups.List.EnableAutomaticBackups.description)
			}

			Section {
				HStack {
					let (warningSymbol, warningSymbolColor) = store.daysSinceLastBackup.warningSymbol()

					Image(systemSymbol: warningSymbol)
						.foregroundStyle(warningSymbolColor)

					switch store.daysSinceLastBackup {
					case .never:
						VStack {
							Text(Strings.Backups.List.NeverBackedUp.title)
								.font(.headline)
							Text(Strings.Backups.List.NeverBackedUp.description)
						}
					case let .days(days):
						if days >= 14 {
							Text(Strings.Backups.List.lastSyncNotWithinTwoWeeks)
						} else {
							Text(Strings.Backups.List.lastSyncWithinTwoWeeks)
						}
					}
				}
			}

			Section {
				Button { send(.didTapManualSyncButton) } label: {
					Text(Strings.Backups.List.manualSync)
						.frame(maxWidth: .infinity, alignment: .center)
				}
				.modifier(PrimaryButton())
			}
			.listRowInsets(EdgeInsets())

			Section(Strings.Backups.List.mostRecent) {
				if store.backups.isEmpty {
					Text(Strings.Backups.List.MostRecent.none)
				}

				ForEach(store.backups) { backup in
					Button {
						send(.didTapBackupFile(backup.id))
					} label: {
						BackupFileView(file: backup, isLatest: backup == store.backups.first)
					}
					.buttonStyle(.navigation)
					.swipeActions(allowsFullSwipe: true) {
						DeleteButton { send(.didSwipe(backup.id, .delete)) }
					}
				}
			}
		}
		.navigationTitle(Strings.Backups.List.title)
		.task { await send(.didStartTask).finish() }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}
}

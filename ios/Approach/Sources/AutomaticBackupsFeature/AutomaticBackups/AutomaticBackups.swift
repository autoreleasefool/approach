import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import ToastLibrary

@Reducer
public struct AutomaticBackups: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Presents public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum View {
			case didStartTask
		}
		@CasePathable public enum Internal {
			case didCreateBackup(Result<BackupFile?, Error>)

			case destination(PresentationAction<Destination.Action>)
		}
		@CasePathable public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case backupFailure(BackupFailure.State)
			case toast(ToastState<ToastAction>)
		}

		public enum Action {
			case backupFailure(BackupFailure.Action)
			case toast(ToastAction)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.backupFailure, action: \.backupFailure) {
				BackupFailure()
			}
		}
	}

	public enum ToastAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
		case didTapOpenBackupsButton
	}

	public init() {}

	@Dependency(\.analytics) var analytics
	@Dependency(BackupsService.self) var backups
	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.featureFlags) var featureFlags
	@Dependency(\.preferences) var preferences

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .run { send in
						guard self.backups.isEnabled() else { return }

						let lastBackupDate = backups.lastSuccessfulBackupDate() ?? .distantPast

						let dayOfBackup = calendar.startOfDay(for: lastBackupDate)
						guard date().timeIntervalSince(dayOfBackup) > BackupsService.MINIMUM_SECONDS_BETWEEN_BACKUPS else { return }

						await send(.internal(.didCreateBackup(Result {
							try await backups.createBackup()
						})))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCreateBackup(.success(.some)):
					state.destination = .toast(
						ToastState(
							content: .toast(SnackContent(message: Strings.Backups.Toast.Success.message)),
							isDimmedBackgroundEnabled: false,
							style: .success
						)
					)
					return .none

				case .didCreateBackup(.success(.none)):
					state.destination = .backupFailure(BackupFailure.State(errorDescription: "Error"))
					return .none

				case let .didCreateBackup(.failure(error)):
					state.destination = .backupFailure(BackupFailure.State(errorDescription: error.localizedDescription))
					return .none

				case let .destination(.presented(.toast(toastAction))):
					switch toastAction {
					case .didTapOpenBackupsButton:
						return .none

					case .didDismiss:
						state.destination = nil
						return .none

					case .didFinishDismissing:
						state.destination = nil
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.backupFailure(.internal))),
						.destination(.presented(.backupFailure(.view))),
						.destination(.presented(.backupFailure(.delegate(.doNothing)))):
					return .none
				}

			case .delegate(.doNothing):
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didCreateBackup(.failure(error))):
				return error
			default:
				return nil
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didCreateBackup(.success(.some(file)))):
				return Analytics.Backups.Created(fileSizeBytes: file.fileSizeBytes)
			default:
				return nil
			}
		}
	}
}

// MARK: - View

public struct AutomaticBackupsViewModifier: ViewModifier {
	@SwiftUI.State var store: StoreOf<AutomaticBackups>

	public func body(content: Content) -> some View {
		content
			.task { await store.send(.view(.didStartTask)).finish() }
			.backupFailure($store.scope(state: \.destination?.backupFailure, action: \.internal.destination.backupFailure))
	}
}

@MainActor extension View {
	fileprivate func backupFailure(_ store: Binding<StoreOf<BackupFailure>?>) -> some View {
		sheet(item: store) { (store: StoreOf<BackupFailure>) in
			BackupFailureView(store: store)
		}
	}
}

extension View {
	public func automaticBackups(store: StoreOf<AutomaticBackups>) -> some View {
		self.modifier(AutomaticBackupsViewModifier(store: store))
	}
}

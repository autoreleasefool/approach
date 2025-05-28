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
		@Presents public var backupFailure: BackupFailure.State?
		@Presents public var toast: ToastState<ToastAction>?

		public init() {}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View {
			case didStartTask
		}
		@CasePathable
		public enum Internal {
			case didCreateBackup(Result<BackupFile?, Error>)

			case backupFailure(PresentationAction<BackupFailure.Action>)
			case toast(PresentationAction<ToastAction>)
		}
		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum ToastAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
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
						guard backups.isEnabled() else { return }

						await send(.internal(.didCreateBackup(Result {
							try await backups.createBackup(skipIfWithinMinimumTime: true)
						})))

						try await backups.cleanUp()
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCreateBackup(.success(.some)):
					state.toast = ToastState(
						content: .toast(SnackContent(message: Strings.Backups.Toast.Success.message)),
						isDimmedBackgroundEnabled: false,
						style: .success
					)
					return .none

				case .didCreateBackup(.success(.none)):
					return .none

				case let .didCreateBackup(.failure(error)):
					state.backupFailure = BackupFailure.State(errorDescription: error.localizedDescription)
					return .none

				case let .toast(.presented(toastAction)):
					switch toastAction {
					case .didDismiss:
						state.toast = nil
						return .none

					case .didFinishDismissing:
						state.toast = nil
						return .none
					}

				case .backupFailure(.dismiss), .toast(.dismiss),
						.backupFailure(.presented(.internal)), .backupFailure(.presented(.view)),
						.backupFailure(.presented(.delegate(.doNothing))):
					return .none
				}

			case .delegate(.doNothing):
				return .none
			}
		}
		.ifLet(\.$backupFailure, action: \.internal.backupFailure) {
			BackupFailure()
		}
		.ifLet(\.$toast, action: \.internal.toast) {}

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
			.backupFailure($store.scope(state: \.backupFailure, action: \.internal.backupFailure))
			.toast($store.scope(state: \.toast, action: \.internal.toast))
	}
}

extension View {
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

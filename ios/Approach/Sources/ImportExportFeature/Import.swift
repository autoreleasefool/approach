import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import EquatablePackageLibrary
import ErrorReportingClientPackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import ImportExportServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@Reducer
public struct Import: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var lastBackupAt: Date?
		public var progress: Progress = .notStarted

		public var isShowingFileImporter = false

		public var errors = Errors<ErrorID>.State()
		@Presents public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didAppear
			case didFirstAppear
			case didTapImportButton
			case didTapRestoreButton
			case didCancelFileImport
			case didImportFiles(Result<[URL], Error>)
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didFetchLatestBackupDate(Result<Date?, Error>)
			case didRestoreBackup(Result<Void, Error>)
			case didImportBackup(Result<Void, Error>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case alert(AlertState<AlertAction>)
	}

	public enum Progress: Equatable {
		case notStarted
		case pickingFile
		case importing
		case restoring
		case failed(AlwaysEqual<Error>)
		case importComplete
		case restoreComplete
	}

	public enum AlertAction: Equatable {
		case didTapRestoreButton
		case didTapDismissButton
	}

	public enum ErrorID: Hashable {
		case failedToImport
		case failedToRestore
		case failedToFetchBackup
	}

	public init() {}

	@Dependency(ImportService.self) var importService

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors<ErrorID>()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none

				case .didTapImportButton:
					state.progress = .pickingFile
					state.isShowingFileImporter = true
					return .none

				case let .didImportFiles(.success(urls)):
					state.isShowingFileImporter = false
					guard let url = urls.first else { return .none }
					state.progress = .importing
					return .run { send in
						await send(.internal(.didImportBackup(Result {
							try await importService.importDatabase(fromUrl: url)
						})))

						await send(.internal(.didFetchLatestBackupDate(Result {
							try await importService.getLatestBackupDate()
						})))
					}

				case let .didImportFiles(.failure(error)):
					state.progress = .failed(AlwaysEqual(error))
					return state.errors
						.enqueue(.failedToImport, thrownError: error, toastMessage: Strings.Error.Toast.failedToImport)
						.map { .internal(.errors($0)) }

				case .didCancelFileImport:
					state.progress = .notStarted
					state.isShowingFileImporter = false
					return .none

				case .didTapRestoreButton:
					guard let lastBackupDate = state.lastBackupAt else { return .none }
					state.progress = .restoring
					state.destination = .alert(Import.restore(toDate: lastBackupDate))
					return .none

				case .didFirstAppear:
					return .run { send in
						await send(.internal(.didFetchLatestBackupDate(Result {
							try await importService.getLatestBackupDate()
						})), animation: .default)
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didFetchLatestBackupDate(.success(date)):
					state.lastBackupAt = date
					return .none

				case .didImportBackup(.success):
					state.progress = .importComplete
					return .none

				case .didRestoreBackup(.success):
					state.progress = .restoreComplete
					return .none

				case let .didFetchLatestBackupDate(.failure(error)):
					state.progress = .failed(AlwaysEqual(error))
					return state.errors
						.enqueue(.failedToFetchBackup, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didImportBackup(.failure(error)):
					state.progress = .failed(AlwaysEqual(error))
					return state.errors
						.enqueue(.failedToImport, thrownError: error, toastMessage: Strings.Error.Toast.failedToImport)
						.map { .internal(.errors($0)) }

				case let .didRestoreBackup(.failure(error)):
					state.progress = .failed(AlwaysEqual(error))
					return state.errors
						.enqueue(.failedToRestore, thrownError: error, toastMessage: Strings.Error.Toast.failedToRestore)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.alert(alertAction))):
					switch alertAction {
					case .didTapRestoreButton:
						return .run { send in
							await send(.internal(.didRestoreBackup(Result {
								try await importService.restoreBackup()
							})))
						}

					case .didTapDismissButton:
						state.progress = .notStarted
						return .none
					}

				case .destination(.dismiss),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.didAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .view(.didImportFiles(.failure(error))),
				let .internal(.didFetchLatestBackupDate(.failure(error))),
				let .internal(.didImportBackup(.failure(error))),
				let .internal(.didRestoreBackup(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

extension Import {
	static func restore(toDate: Date) -> AlertState<AlertAction> {
		AlertState(
			title: TextState(Strings.Import.Restore.title),
			message: TextState(Strings.Import.Restore.message(toDate.mediumFormat)),
			primaryButton: .destructive(
				TextState(Strings.Import.Action.restore),
				action: .send(.didTapRestoreButton)
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}

// MARK: - View

@ViewAction(for: Import.self)
public struct ImportView: View {
	@Bindable public var store: StoreOf<Import>

	public init(store: StoreOf<Import>) {
		self.store = store
	}

	public var body: some View {
		VStack(spacing: 0) {
			List {
				Section {
					VStack(spacing: .standardSpacing) {
						Text(Strings.Import.Description.approachAllowsExport)
							.frame(maxWidth: .infinity, alignment: .leading)

						Text(Strings.Import.Description.importBackupFiles)
							.frame(maxWidth: .infinity, alignment: .leading)
					}
				}

				if store.lastBackupAt != nil {
					Section {
						Text(Strings.Import.Description.restore)
							.frame(maxWidth: .infinity, alignment: .leading)

						Button(Strings.Import.Action.restorePreviousData) {
							send(.didTapRestoreButton)
						}
					}
				}

				ProgressBanner(progress: store.progress)
			}

			Divider()

			OverwriteWarningBanner(progress: store.progress)
				.padding(.horizontal)
				.padding(.top)

			Button {
				send(.didTapImportButton)
			} label: {
				Text(Strings.Import.Action.import)
					.frame(maxWidth: .infinity)
			}
			.modifier(PrimaryButton())
			.padding()
		}
		.navigationTitle(Strings.Import.title)
		.onFirstAppear { send(.didFirstAppear) }
		.onAppear { send(.didAppear) }
		.alert($store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.fileImporter(
			isPresented: $store.isShowingFileImporter,
			allowedContentTypes: [.item],
			allowsMultipleSelection: false,
			onCompletion: { send(.didImportFiles($0)) },
			onCancellation: { send(.didCancelFileImport) }
		)
	}
}

private struct OverwriteWarningBanner: View {
	let progress: Import.Progress

	var body: some View {
		if case .notStarted = progress {
			Banner(
				.titleAndMessage(
					Strings.Import.Instructions.overwrite,
					Strings.Import.Instructions.notRecover
				),
				style: .warning
			)
		}
	}
}

private struct ProgressBanner: View {
	let progress: Import.Progress

	var body: some View {
		Section {
			switch progress {
			case .notStarted, .pickingFile:
				EmptyView()
			case .importing, .restoring:
				Banner(.message(Strings.Import.Importing.inProgress), style: .plain)
			case let .failed(error):
				Banner(
					.titleAndMessage(
						Strings.Import.Importing.error,
						error.localizedDescription
					),
					style: .error
				)
			case .importComplete:
				Banner(.message(Strings.Import.Importing.successImporting), style: .success)
			case .restoreComplete:
				Banner(.message(Strings.Import.Importing.successRestoring), style: .success)
			}
		}
		.listRowInsets(EdgeInsets())
	}
}

#Preview {
	NavigationStack {
		ImportView(
			store: Store(
				initialState: Import.State(),
				reducer: { Import() }
			)
		)
	}
}

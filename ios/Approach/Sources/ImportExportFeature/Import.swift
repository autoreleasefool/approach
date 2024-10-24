import AnalyticsServiceInterface
import AppInfoPackageServiceInterface
import AssetsLibrary
import ComposableArchitecture
import DateTimeLibrary
import Dependencies
import EmailServiceInterface
import EquatablePackageLibrary
import ErrorReportingClientPackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import HUDServiceInterface
import ImportExportServiceInterface
import PasteboardPackageServiceInterface
import PreferenceServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ToastLibrary
import ViewsLibrary

@Reducer
public struct Import: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var lastBackupAt: Date?
		public var progress: Progress = .notStarted
		public let appVersion: String

		public var isShowingFileImporter = false

		public var isShowingEmail = false
		public var errors = Errors<ErrorID>.State()
		@Presents public var toast: ToastState<ToastAction>?
		@Presents public var destination: Destination.State?

		public init() {
			@Dependency(\.appInfo) var appInfo
			self.appVersion = appInfo.getFullAppVersion()
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case didAppear
			case didFirstAppear
			case didTapImportButton
			case didTapRestoreButton
			case didCancelFileImport
			case didTapSendEmailButton
			case didTapCopyEmailButton
			case didImportFiles(Result<[URL], Error>)
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didFetchLatestBackupDate(Result<Date?, Error>)
			case didRestoreBackup(Result<Void, Error>)
			case didImportBackup(Result<ImportResult, Error>)
			case didCopyEmailToClipboard(Result<Void, Error>)

			case errors(Errors<ErrorID>.Action)
			case toast(PresentationAction<ToastAction>)
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
		case importComplete(ImportResult)
		case restoreComplete
	}

	public enum AlertAction: Equatable {
		case didTapRestoreButton
		case didTapDismissButton
	}

	public enum ToastAction: ToastableAction {
		case didDismiss
		case didFinishDismissing
	}

	public enum ErrorID: Hashable {
		case failedToImport
		case failedToRestore
		case failedToFetchBackup
	}

	enum HUD: Hashable, Sendable {
		case importing
	}

	public init() {}

	@Dependency(EmailService.self) var email
	@Dependency(HUDService.self) var hud
	@Dependency(ImportService.self) var importService
	@Dependency(\.openURL) var openURL
	@Dependency(\.pasteboard) var pasteboard

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
			Errors<ErrorID>()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .none

				case .didTapSendEmailButton:
					return .run { send in
						if await email.canSendEmail() {
							await send(.binding(.set(\.isShowingEmail, true)))
						} else {
							guard let mailto = URL(string: "mailto://\(Strings.supportEmail)") else { return }
							await openURL(mailto)
						}
					}

				case .didTapCopyEmailButton:
					return .run { send in
						await send(.internal(.didCopyEmailToClipboard(Result {
							try await pasteboard.copyToClipboard(Strings.supportEmail)
						})))
					}

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

				case let .didImportBackup(.success(result)):
					state.progress = .importComplete(result)
					return .none

				case .didRestoreBackup(.success):
					state.progress = .restoreComplete
					return .none

				case .didCopyEmailToClipboard(.success):
					state.toast = ToastState(
						content: .toast(SnackContent(message: Strings.copiedToClipboard)),
						isDimmedBackgroundEnabled: false,
						style: .primary
					)
					return .none

				case .didCopyEmailToClipboard(.failure):
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

				case .toast(.dismiss):
					state.toast = nil
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

				case let .destination(.presented(.alert(alertAction))):
					switch alertAction {
					case .didTapRestoreButton:
						state.progress = .restoring
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
		.ifLet(\.$toast, action: \.internal.toast) {}
		.onChange(of: \.progress) { _, progress in
			Reduce<State, Action> { _, _ in
				switch progress {
				case .importing, .restoring:
					return .run { _ in await hud.requestHUD(HUD.importing, style: .loading) }
				case .failed, .importComplete, .notStarted, .restoreComplete, .pickingFile:
					return .run { _ in await hud.dismissHUD(HUD.importing) }
				}
			}
		}

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
		AlertState {
			TextState(Strings.Import.Restore.title)
		} actions: {
			ButtonState(role: .destructive, action: .send(.didTapRestoreButton)) {
				TextState(Strings.Import.Action.restore)
			}

			ButtonState(role: .cancel, action: .send(.didTapDismissButton)) {
				TextState(Strings.Action.cancel)
			}
		} message: {
			TextState(Strings.Import.Restore.message(toDate.mediumFormat))
		}
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

				ImportProgressBanner(progress: store.progress) {
					send(.didTapSendEmailButton)
				} onDidTapCopyEmail: {
					send(.didTapCopyEmailButton)
				}
			}

			Divider()

			ImportOverwriteWarningBanner(progress: store.progress)
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
		.toast($store.scope(state: \.toast, action: \.internal.toast))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.fileImporter(
			isPresented: $store.isShowingFileImporter,
			allowedContentTypes: [.item],
			allowsMultipleSelection: false,
			onCompletion: { send(.didImportFiles($0)) },
			onCancellation: { send(.didCancelFileImport) }
		)
		.sheet(isPresented: $store.isShowingEmail) {
			EmailView(
				content: .init(
					recipients: [Strings.supportEmail],
					subject: Strings.Import.Importing.Report.emailSubject(store.appVersion)
				)
			)
		}
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

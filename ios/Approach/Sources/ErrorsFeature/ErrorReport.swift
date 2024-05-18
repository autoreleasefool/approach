import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import EquatableLibrary
import FeatureActionLibrary
import FileManagerServiceInterface
import PasteboardPackageServiceInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct ErrorReport: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var isIncludingDeviceLogs = true
		public var isShowingEmailReport: Bool = false

		public let thrownError: AlwaysEqual<Error>
		public let additionalErrors: AlwaysEqual<[Error]>
		public let logDataUrl: URL?
		public let canSendEmail: Bool

		@Presents public var alert: AlertState<AlertAction>?

		var logData: Data? {
			guard let logDataUrl else { return nil }
			@Dependency(FileManagerService.self) var fileManager
			return try? fileManager.getFileContents(logDataUrl)
		}

		var allErrors: [Error] {
			[thrownError.wrapped] + additionalErrors.wrapped
		}

		init(
			thrownError: Error,
			additionalErrors: [Error],
			logDataUrl: URL?,
			canSendEmail: Bool
		) {
			self.thrownError = .init(thrownError)
			self.additionalErrors = .init(additionalErrors)
			self.logDataUrl = logDataUrl
			self.canSendEmail = canSendEmail
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapCopyErrorButton
			case didTapDismissButton
			case didTapEmailButton
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didCopyToClipboard
			case alert(PresentationAction<AlertAction>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum AlertAction: Equatable {
		case didTapDismissButton
	}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.openURL) var openURL
	@Dependency(PasteboardService.self) var pasteboard

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapCopyErrorButton:
					return .run { [errorDescription = state.thrownError.wrapped.localizedDescription] send in
						try await pasteboard.copyToClipboard(errorDescription)
						await send(.internal(.didCopyToClipboard))
					}

				case .didTapDismissButton:
					return .run { _ in await dismiss() }

				case .didTapEmailButton:
					if state.canSendEmail {
						state.isShowingEmailReport = true
						return .none
					} else {
						return .run { _ in
							guard let mailto = URL(string: "mailto://\(Strings.Settings.Help.ReportBug.email)") else { return }
							await openURL(mailto)
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCopyToClipboard:
					state.alert = AlertState { TextState(Strings.copiedToClipboard) }
					return .none

				case .alert:
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

@ViewAction(for: ErrorReport.self)
public struct ErrorReportView: View {
	@Bindable public var store: StoreOf<ErrorReport>

	public var body: some View {
		VStack {
			HStack {
				Spacer()
				Button {
					send(.didTapDismissButton)
				} label: {
					Image(systemSymbol: .xmark)
						.resizable()
						.scaledToFit()
						.frame(width: .extraTinyIcon, height: .extraTinyIcon)
						.foregroundColor(.white)
						.padding(.smallSpacing)
						.background(
							Circle()
								.fill(.gray)
						)
				}
			}

			ScrollView {
				VStack(alignment: .leading, spacing: .standardSpacing) {
					Text(Strings.ErrorReport.reportingAnError)
						.font(.title)
						.bold()

					Text(Strings.ErrorReport.youveEncountered)

					Button {
						send(.didTapCopyErrorButton)
					} label: {
						VStack {
							Text(store.thrownError.wrapped.localizedDescription)
								.multilineTextAlignment(.leading)

							HStack {
								Spacer()
								Image(systemSymbol: .squareOnSquare)
									.resizable()
									.scaledToFit()
									.frame(width: .tinyIcon)
							}
						}
						.foregroundColor(.white)
						.padding(.smallSpacing)
						.background(
							RoundedRectangle(cornerRadius: .standardRadius)
								.fill(Asset.Colors.Error.default.swiftUIColor)
						)
						.shadow(radius: .smallRadius)
					}

					Text(Strings.ErrorReport.maybeWeCanHelp)
						.font(.headline)
				}
			}

			Spacer()

			if store.canSendEmail {
				GroupBox {
					VStack(spacing: .smallSpacing) {
						Toggle(
							Strings.ErrorReport.includeDeviceLogs,
							isOn: $store.isIncludingDeviceLogs
						)

						Text(Strings.ErrorReport.IncludeDeviceLogs.disclaimer)
							.font(.caption2)
							.foregroundColor(.gray)
					}
				}
			}

			VStack(spacing: .standardSpacing) {
				Button {
					send(.didTapEmailButton)
				} label: {
					Text(Strings.ErrorReport.emailReport)
						.frame(maxWidth: .infinity)
				}
				.modifier(PrimaryButton())

				if let logDataUrl = store.logDataUrl {
					ShareLink(item: logDataUrl) {
						Text(Strings.ErrorReport.shareReport)
					}
					.disabled(!store.isIncludingDeviceLogs)
				}
			}
		}
		.padding()
		.onAppear { send(.onAppear) }
		.alert($store.scope(state: \.alert, action: \.internal.alert))
		.sheet(isPresented: $store.isShowingEmailReport) {
			EmailView(
				content: .init(
					recipients: [Strings.Settings.Help.ReportBug.email],
					subject: Strings.Settings.Help.ReportBug.subject(AppConstants.appVersionReadable),
					body: Strings.ErrorReport.emailBody(
						store
							.allErrors
							.map { $0.localizedDescription }
							.joined(separator: "\n- ")
					),
					attachment: .init(data: store.isIncludingDeviceLogs ? store.logData : nil)
				)
			)
		}
	}
}

extension EmailView.Content.Attachment {
	init?(data: Data?) {
		guard let data else { return nil }
		self.init(data: data, mimeType: "application/zip", fileName: "approach_logs.zip")
	}
}

#if DEBUG
enum ErrorReportMockError: Error {
	case mockError
}

struct ErrorReportPreview: PreviewProvider {
	static var previews: some View {
		ErrorReportView(
			store: .init(
				initialState: .init(
					thrownError: ErrorReportMockError.mockError,
					additionalErrors: [],
					logDataUrl: URL(string: "https://example.com"),
					canSendEmail: true
				),
				reducer: ErrorReport.init
			)
		)
	}
}
#endif

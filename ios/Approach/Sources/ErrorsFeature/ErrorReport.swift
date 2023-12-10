import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ConstantsLibrary
import EquatableLibrary
import FeatureActionLibrary
import FileManagerServiceInterface
import PasteboardServiceInterface
import StringsLibrary
import SwiftUI
import ToastLibrary
import ViewsLibrary

public struct ErrorReport: Reducer {
	public struct State: Equatable {
		@BindingState public var isIncludingDeviceLogs = true
		@BindingState public var isShowingEmailReport: Bool = false

		public var toast: ToastState<ToastAction>?

		public let thrownError: AlwaysEqual<Error>
		public let additionalErrors: AlwaysEqual<[Error]>
		public let logDataUrl: URL?
		public let canSendEmail: Bool

		var logData: Data? {
			guard let logDataUrl else { return nil }
			@Dependency(\.fileManager) var fileManager
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case didTapCopyErrorButton
			case didTapDismissButton
			case didTapEmailButton
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable { case doNothing }
		public enum InternalAction: Equatable {
			case didCopyToClipboard
			case toast(ToastAction)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum ToastAction: ToastableAction, Equatable {
		case didDismiss
		case didFinishDismissing
	}

	@Dependency(\.dismiss) var dismiss
	@Dependency(\.openURL) var openURL
	@Dependency(\.pasteboard) var pasteboard

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapCopyErrorButton:
					return .run { [errorDescription = state.thrownError.wrapped.localizedDescription] send in
						pasteboard.copyToClipboard(errorDescription)
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

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didCopyToClipboard:
					state.toast = .init(
						content: .toast(.init(
							message: .init(Strings.copiedToClipboard),
							icon: .checkmarkCircleFill
						)),
						style: .success
					)
					return .none

				case .toast(.didDismiss):
					state.toast = nil
					return .none

				case .toast(.didFinishDismissing):
					return .none
				}

			case .delegate:
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

public struct ErrorReportView: View {
	let store: StoreOf<ErrorReport>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			VStack {
				HStack {
					Spacer()
					Button {
						viewStore.send(.didTapDismissButton)
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
							viewStore.send(.didTapCopyErrorButton)
						} label: {
							VStack {
								Text(viewStore.thrownError.wrapped.localizedDescription)
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

				if viewStore.canSendEmail {
					GroupBox {
						VStack(spacing: .smallSpacing) {
							Toggle(
								Strings.ErrorReport.includeDeviceLogs,
								isOn: viewStore.$isIncludingDeviceLogs
							)

							Text(Strings.ErrorReport.IncludeDeviceLogs.disclaimer)
								.font(.caption2)
								.foregroundColor(.gray)
						}
					}
				}

				VStack(spacing: .standardSpacing) {
					Button {
						viewStore.send(.didTapEmailButton)
					} label: {
						Text(Strings.ErrorReport.emailReport)
							.frame(maxWidth: .infinity)
					}
					.modifier(PrimaryButton())

					if let logDataUrl = viewStore.logDataUrl {
						ShareLink(item: logDataUrl) {
							Text(Strings.ErrorReport.shareReport)
						}
						.disabled(!viewStore.isIncludingDeviceLogs)
					}
				}
			}
			.padding()
			.sheet(isPresented: viewStore.$isShowingEmailReport) {
				EmailView(
					content: .init(
						recipients: [Strings.Settings.Help.ReportBug.email],
						subject: Strings.Settings.Help.ReportBug.subject(AppConstants.appVersionReadable),
						body: Strings.ErrorReport.emailBody(
							viewStore
								.allErrors
								.map { $0.localizedDescription }
								.joined(separator: "\n- ")
						),
						attachment: .init(data: viewStore.isIncludingDeviceLogs ? viewStore.logData : nil)
					)
				)
			}
			.onAppear { viewStore.send(.onAppear) }
		})
		.toast(store: store.scope(state: \.toast, action: { .internal(.toast($0)) }))
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

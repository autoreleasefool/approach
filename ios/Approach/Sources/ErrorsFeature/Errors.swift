import AssetsLibrary
import ComposableArchitecture
import EmailServiceInterface
import EquatablePackageLibrary
import FeatureActionLibrary
import Foundation
import LoggingServiceInterface
import StringsLibrary
import SwiftUI
import ToastLibrary

private let ERROR_REPORT_THRESHOLD = 3

@Reducer
public struct Errors<ErrorID: Hashable>: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var errorCount: [ErrorID: Int] = [:]
		public var toastQueue: [ToastState<ToastAction>] = []

		@Presents public var destination: Destination.State?

		public init() {}

		public mutating func enqueue(_ error: Errors.ErrorToastState) -> Effect<Errors.Action> {
			let timesSeen = errorCount[error.id] ?? 1
			errorCount[error.id] = timesSeen + 1

			let reportButton: ToastState<ToastAction>.Button?
			if timesSeen >= ERROR_REPORT_THRESHOLD {
				reportButton = .init(
					title: Strings.Error.Toast.tapToReport,
					action: .didTapReportFeedbackButton(error.thrownError)
				)
			} else {
				reportButton = nil
			}

			let toast: ToastState<ToastAction> = .init(
				content: .toast(.init(
					message: error.message,
					icon: error.icon,
					button: reportButton
				)),
				isDimmedBackgroundEnabled: false,
				style: .error
			)

			if self.destination == nil {
				self.destination = .toast(toast)
			} else {
				// Insert at 0 because we popLast() for FIFO
				self.toastQueue.insert(toast, at: 0)
			}

			return .none
		}

		public mutating func enqueue(_ errorId: ErrorID, thrownError: Error, toastMessage: String) -> Effect<Errors.Action> {
			enqueue(.init(id: errorId, thrownError: thrownError, message: .init(toastMessage), icon: .exclamationmarkTriangle))
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum View {
			case didFinishDismissingReport
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case didReceiveReport(AlwaysEqual<Report>)
			case showNextToast
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case report(ErrorReport.State)
			case toast(ToastState<ToastAction>)
		}

		public enum Action {
			case report(ErrorReport.Action)
			case toast(ToastAction)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.report, action: \.report) {
				ErrorReport()
			}
		}
	}

	public enum ToastAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
		case didTapReportFeedbackButton(AlwaysEqual<Error>)
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(EmailService.self) var email
	@Dependency(LoggingService.self) var logging

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFinishDismissingReport:
					state.destination = nil
					return .run { send in
						try await clock.sleep(for: .seconds(1))
						await send(.internal(.showNextToast))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showNextToast:
					if let toast = state.toastQueue.popLast() {
						state.destination = .toast(toast)
					}
					return .none

				case let .didReceiveReport(report):
					state.destination = .report(.init(
						thrownError: report.wrapped.thrownError,
						additionalErrors: report.wrapped.additionalErrors,
						logDataUrl: report.wrapped.logDataUrl,
						canSendEmail: report.wrapped.canSendEmail
					))
					return .none

				case .destination(.presented(.report(.delegate(.doNothing)))):
					return .none

				case let .destination(.presented(.toast(toastAction))):
					switch toastAction {
					case let .didTapReportFeedbackButton(thrownError):
						state.destination = nil
						return .run { send in
							let logDataUrl = try await logging.fetchLogData()
							await send(.internal(.didReceiveReport(.init(.init(
								thrownError: thrownError.wrapped,
								additionalErrors: [],
								logDataUrl: logDataUrl,
								canSendEmail: email.canSendEmail()
							)))))
						} catch: { error, send in
							await send(.internal(.didReceiveReport(.init(.init(
								thrownError: thrownError.wrapped,
								additionalErrors: [error],
								logDataUrl: nil,
								canSendEmail: email.canSendEmail()
							)))))
						}

					case .didDismiss:
						state.destination = nil
						return .none

					case .didFinishDismissing:
						switch state.destination {
						case .report:
							return .none
						case .toast, .none:
							break
						}

						state.destination = nil
						return .run { send in
							try await clock.sleep(for: .seconds(1))
							await send(.internal(.showNextToast))
						}
					}

				case .destination(.dismiss),
						.destination(.presented(.report(.internal))),
						.destination(.presented(.report(.view))),
						.destination(.presented(.report(.binding))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}
	}
}

extension Errors {
	public struct ErrorToastState: Identifiable, Equatable {
		public let id: ErrorID
		public let message: String
		public let icon: SFSymbol?
		public let thrownError: AlwaysEqual<Error>

		public init(id: ErrorID, thrownError: Error, message: String, icon: SFSymbol?) {
			self.id = id
			self.message = message
			self.icon = icon
			self.thrownError = .init(thrownError)
		}
	}
}

extension Errors {
	public struct Report {
		public let thrownError: Error
		public let additionalErrors: [Error]
		public let logDataUrl: URL?
		public let canSendEmail: Bool
	}
}

// MARK: - View

public struct ErrorsViewModifier<ErrorID: Hashable>: ViewModifier {
	@SwiftUI.State var store: StoreOf<Errors<ErrorID>>

	public func body(content: Content) -> some View {
		content
			.toast($store.scope(state: \.destination?.toast, action: \.internal.destination.toast))
			.sheet(
				item: $store.scope(state: \.destination?.report, action: \.internal.destination.report),
				onDismiss: { store.send(.view(.didFinishDismissingReport)) },
				content: { store in
					ErrorReportView(store: store)
				}
			)
	}
}

extension View {
	public func errors<ErrorID: Hashable>(store: StoreOf<Errors<ErrorID>>) -> some View {
		self.modifier(ErrorsViewModifier(store: store))
	}
}

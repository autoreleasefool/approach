import AssetsLibrary
import ComposableArchitecture
import EmailServiceInterface
import EquatableLibrary
import FeatureActionLibrary
import Foundation
import LoggingServiceInterface
import StringsLibrary
import SwiftUI
import ToastLibrary

private let ERROR_REPORT_THRESHOLD = 3

@Reducer
public struct Errors<ErrorID: Hashable>: Reducer {
	public struct State: Equatable {
		public var errorCount: [ErrorID: Int] = [:]
		public var currentToast: ToastState<ToastAction>?
		public var toastQueue: [ToastState<ToastAction>] = []

		@PresentationState public var report: ErrorReport.State?

		public init() {}

		public mutating func enqueue(_ error: Errors.ErrorToastState) -> Effect<Errors.Action> {
			let timesSeen = errorCount[error.id] ?? 1
			errorCount[error.id] = timesSeen + 1

			let toast: ToastState<Errors.ToastAction> = .init(
				from: error,
				withReportButton: timesSeen >= ERROR_REPORT_THRESHOLD
			)

			if self.currentToast == nil {
				self.currentToast = toast
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

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFinishDismissingReport
		}
		public enum DelegateAction: Equatable { case doNothing }
		public enum InternalAction: Equatable {
			case didReceiveReport(AlwaysEqual<Report>)

			case toast(ToastAction)
			case report(PresentationAction<ErrorReport.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum ToastAction: ToastableAction, Equatable {
		case didReportFeedback(AlwaysEqual<Error>)
		case didDismiss
		case didFinishDismissing
	}

	public init() {}

	@Dependency(\.email) var email
	@Dependency(\.logging) var logging

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFinishDismissingReport:
					state.currentToast = state.toastQueue.popLast()
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didReceiveReport(report):
					state.report = .init(
						thrownError: report.wrapped.thrownError,
						additionalErrors: report.wrapped.additionalErrors,
						logDataUrl: report.wrapped.logDataUrl,
						canSendEmail: report.wrapped.canSendEmail
					)
					return .none

				case .report(.presented(.delegate(.doNothing))):
					return .none

				case let .toast(toastAction):
					switch toastAction {
					case let .didReportFeedback(thrownError):
						state.currentToast = nil
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
						state.currentToast = nil
						return .none

					case .didFinishDismissing:
						guard state.report == nil else { return .none }
						state.currentToast = state.toastQueue.popLast()
						return .none
					}

				case .report(.presented(.internal)), .report(.presented(.view)), .report(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$report, action: /Action.internal..Action.InternalAction.report) {
			ErrorReport()
		}
	}
}

extension Errors {
	public struct ErrorToastState: Identifiable, Equatable {
		public let id: ErrorID
		public let message: TextState
		public let icon: SFSymbol?
		public let thrownError: AlwaysEqual<Error>

		public init(id: ErrorID, thrownError: Error, message: TextState, icon: SFSymbol?) {
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

extension ToastState {
	init<ErrorID: Hashable>(
		from error: Errors<ErrorID>.ErrorToastState,
		withReportButton: Bool
	) where Action == Errors<ErrorID>.ToastAction {
		self.init(
			content: .toast(.init(
				message: error.message,
				icon: error.icon,
				button: withReportButton ? .init(
					title: .init(Strings.Action.report),
					action: .init(action: .didReportFeedback(error.thrownError))
				) : nil
			)),
			style: .error
		)
	}
}

// MARK: - View

extension View {
	public func errors<ErrorID: Hashable>(
		store: Store<Errors<ErrorID>.State, Errors<ErrorID>.Action>
	) -> some View {
		self
			.toast(store: store.scope(state: \.currentToast, action: { .internal(.toast($0)) }))
			.sheet(
				store: store.scope(state: \.$report, action: { .internal(.report($0)) }),
				onDismiss: { store.send(.view(.didFinishDismissingReport)) },
				content: { store in
					ErrorReportView(store: store)
				}
			)
	}
}

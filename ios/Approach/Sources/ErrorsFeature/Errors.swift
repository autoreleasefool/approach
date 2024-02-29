import AssetsLibrary
import ComposableArchitecture
import EmailServiceInterface
import EquatableLibrary
import FeatureActionLibrary
import Foundation
import LoggingServiceInterface
import StringsLibrary
import SwiftUI

private let ERROR_REPORT_THRESHOLD = 3

@Reducer
public struct Errors<ErrorID: Hashable>: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var errorCount: [ErrorID: Int] = [:]
		public var alertQueue: [AlertState<AlertAction>] = []

		@Presents public var destination: Destination.State?

		public init() {}

		public mutating func enqueue(_ error: Errors.ErrorAlertState) -> Effect<Errors.Action> {
			let timesSeen = errorCount[error.id] ?? 1
			errorCount[error.id] = timesSeen + 1

			let alert: AlertState<AlertAction> = AlertState {
				error.message
			} actions: {
				if timesSeen >= ERROR_REPORT_THRESHOLD {
					ButtonState(action: .didTapReportFeedbackButton(error.thrownError)) {
						TextState(Strings.Action.report)
					}
				}

				ButtonState(role: .cancel, action: .didTapDismissButton) {
					TextState(Strings.Action.dismiss)
				}
			}

			if self.destination == nil {
				self.destination = .alert(alert)
			} else {
				// Insert at 0 because we popLast() for FIFO
				self.alertQueue.insert(alert, at: 0)
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
			case showNextAlert
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case report(ErrorReport)
		case alert(AlertState<AlertAction>)
	}

	public enum AlertAction: Equatable {
		case didTapReportFeedbackButton(AlwaysEqual<Error>)
		case didTapDismissButton
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.email) var email
	@Dependency(\.logging) var logging

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFinishDismissingReport:
					state.destination = nil
					return .run { send in
						try await clock.sleep(for: .seconds(1))
						await send(.internal(.showNextAlert))
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showNextAlert:
					if let alert = state.alertQueue.popLast() {
						state.destination = .alert(alert)
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

				case let .destination(.presented(.alert(alertAction))):
					switch alertAction {
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

					case .didTapDismissButton:
						state.destination = nil
						return .send(.internal(.showNextAlert))
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
		.ifLet(\.$destination, action: \.internal.destination)
	}
}

extension Errors {
	public struct ErrorAlertState: Identifiable, Equatable {
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

// MARK: - View

public struct ErrorsViewModifier<ErrorID: Hashable>: ViewModifier {
	@SwiftUI.State var store: StoreOf<Errors<ErrorID>>

	public func body(content: Content) -> some View {
		content
			.alert($store.scope(state: \.destination?.alert, action: \.internal.destination.alert))
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

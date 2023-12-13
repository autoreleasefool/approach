import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import LaneEditorFeature
import LanesRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct AlleyLanesEditor: Reducer {
	public struct State: Equatable {
		public var alley: Alley.ID
		public var existingLanes: IdentifiedArrayOf<Lane.Edit>
		public var newLanes: IdentifiedArrayOf<Lane.Create>

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var alert: AlertState<AlertAction>?
		@PresentationState public var addLaneForm: AddLaneForm.State?

		public init(
			alley: Alley.ID,
			existingLanes: IdentifiedArrayOf<Lane.Edit>,
			newLanes: IdentifiedArrayOf<Lane.Create>
		) {
			self.alley = alley
			self.newLanes = newLanes
			self.existingLanes = existingLanes
		}
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case onAppear
			case didTapAddLaneButton
			case didTapAddMultipleLanesButton
			case alert(PresentationAction<AlertAction>)
		}

		@CasePathable public enum DelegateAction { case doNothing }

		@CasePathable public enum InternalAction {
			case didDeleteLane(Result<Lane.ID, Error>)

			case errors(Errors<ErrorID>.Action)
			case laneEditor(IdentifiedActionOf<LaneEditor>)
			case addLaneForm(PresentationAction<AddLaneForm.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton(Lane.ID)
		case didTapDismissButton
	}

	public enum ErrorID: Hashable {
		case failedToDeleteLane
	}

	public enum Field: Hashable {
		case lane(Lane.ID)
	}

	public init() {}

	@Dependency(\.lanes) var lanes
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAddLaneButton:
					// FIXME: is it possible to focus on this lane's input when it appears
					return didFinishAddingLanes(&state, count: 1)

				case .didTapAddMultipleLanesButton:
					state.addLaneForm = .init()
					return .none

				case let .alert(.presented(alertAction)):
					switch alertAction {
					case let .didTapDeleteButton(lane):
						return .run { send in
							await send(.internal(.didDeleteLane(Result {
								try await lanes.delete([lane])
								return lane
							})))
						}

					case .didTapDismissButton:
						state.alert = nil
						return .none
					}

				case .alert(.dismiss):
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didDeleteLane(.success(id)):
					state.existingLanes.removeAll { $0.id == id }
					state.newLanes.removeAll { $0.id == id }
					return .none

				case let .didDeleteLane(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteLane, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .laneEditor(.element(id, .delegate(delegateAction))):
					switch delegateAction {
					case .didDeleteLane:
						if let deleted = state.existingLanes.first(where: { $0.id == id }) {
							state.alert = AlertState {
								TextState(Strings.Form.Prompt.delete(deleted.label))
							} actions: {
								ButtonState(role: .destructive, action: .didTapDeleteButton(deleted.id)) { TextState(Strings.Action.delete) }
								ButtonState(role: .cancel, action: .didTapDismissButton) { TextState(Strings.Action.cancel) }
							}
						} else {
							state.existingLanes.removeAll { $0.id == id }
							state.newLanes.removeAll { $0.id == id }
						}
						return .none
					}

				case let .addLaneForm(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didFinishAddingLanes(numberOfLanes):
						return didFinishAddingLanes(&state, count: numberOfLanes)
					}

				case .addLaneForm(.presented(.internal)), .addLaneForm(.presented(.view)), .addLaneForm(.dismiss),
						.laneEditor(.element(_, .view)), .laneEditor(.element(_, .internal)),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.forEach(\.existingLaneEditors, action: \.internal.laneEditor) {
			LaneEditor()
		}
		.forEach(\.newLaneEditors, action: \.internal.laneEditor) {
			LaneEditor()
		}
		.ifLet(\.$addLaneForm, action: \.internal.addLaneForm) {
			AddLaneForm()
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

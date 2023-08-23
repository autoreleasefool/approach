import AlleyEditorFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct AlleysOverview: Reducer {
	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		init() {}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didSwipeAlley(SwipeAction, Alley.Summary)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case alleysResponse(TaskResult<[Alley.Summary]>)
			case didLoadEditableAlley(TaskResult<Alley.EditWithLanes>)
			case didDeleteAlley(TaskResult<Alley.Summary>)
			case didRequestDeleteAlley(Alley.Summary)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public enum SwipeAction: Equatable {
		case edit
		case delete
	}

	public struct Destination: Reducer {
		public enum State: Equatable {
			case alleyEditor(AlleyEditor.State)
			case alert(AlertState<AlertAction>)
		}

		public enum Action: Equatable {
			case alleyEditor(AlleyEditor.Action)
			case alert(AlertAction)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.alleyEditor, action: /Action.alleyEditor) {
				AlleyEditor()
			}
		}
	}

	enum CancelID { case observeAlleys }

	public enum ErrorID: Hashable {
		case alleyNotFound
		case loadingAlleysFailed
		case failedToDeleteAlley
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton(Alley.Summary)
		case didTapDismissButton
	}

	@Dependency(\.alleys) var alleys

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return observeAlleys()

				case let .didSwipeAlley(action, alley):
					switch action {
					case .edit:
						return .run { [id = alley.id] send in
							await send(.internal(.didLoadEditableAlley(TaskResult {
								try await self.alleys.edit(id)
							})))
						}

					case .delete:
						state.destination = .alert(Self.alert(toDelete: alley))
						return .none
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .alleysResponse(.success(alleys)):
					state.recentAlleys = .init(uniqueElements: alleys)
					return .none

				case let .didLoadEditableAlley(.success(alley)):
					state.destination = .alleyEditor(.init(value: .edit(alley)))
					return .none

				case .didDeleteAlley(.success):
					return .none

				case let .didRequestDeleteAlley(alley):
					return .run { send in
						await send(.internal(.didDeleteAlley(TaskResult {
							try await self.alleys.delete(alley.id)
							return alley
						})))
					}

				case let .alleysResponse(.failure(error)):
					return state.errors
						.enqueue(.loadingAlleysFailed, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableAlley(.failure(error)):
					return state.errors
						.enqueue(.alleyNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteAlley(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteAlley, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.alert(.didTapDeleteButton(alley)))):
					state.destination = nil
					return .send(.internal(.didRequestDeleteAlley(alley)))

				case let .destination(.presented(.alleyEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .errors(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alert(.didTapDismissButton))),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: /Action.internal..Action.InternalAction.destination) {
			Destination()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .view(.didSwipeAlley(.delete, _)):
				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}
	}

	private func observeAlleys() -> Effect<Action> {
		.run { send in
			for try await alleys in self.alleys.overview() {
				await send(.internal(.alleysResponse(.success(alleys))))
			}
		} catch: { error, send in
			await send(.internal(.alleysResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeAlleys, cancelInFlight: true)
	}
}

extension AlleysOverview {
	static func alert(toDelete alley: Alley.Summary) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(alley.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteButton(alley))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}

public struct AlleysOverviewView: View {
	let store: StoreOf<AlleysOverview>

	struct ViewState: Equatable {
		let alleys: IdentifiedArrayOf<Alley.Summary>

		init(state: AlleysOverview.State) {
			self.alleys = state.recentAlleys
		}
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Group {
				if viewStore.alleys.isEmpty {
					Text(Strings.Alley.Error.Empty.message)
				} else {
					// TODO: show empty state for no alleys
					ForEach(viewStore.alleys) { alley in
						Alley.View(alley: alley)
							.swipeActions(allowsFullSwipe: true) {
								EditButton { viewStore.send(.didSwipeAlley(.edit, alley)) }
								DeleteButton { viewStore.send(.didSwipeAlley(.delete, alley)) }
							}
					}
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /AlleysOverview.Destination.State.alleyEditor,
			action: AlleysOverview.Destination.Action.alleyEditor
		) { (store: StoreOf<AlleyEditor>) in
			NavigationStack {
				AlleyEditorView(store: store)
			}
		}
	}
}

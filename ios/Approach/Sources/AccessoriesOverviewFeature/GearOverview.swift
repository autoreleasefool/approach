import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearEditorFeature
import GearListFeature
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct GearOverview: Reducer {
	public struct State: Equatable {
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		init() {}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didObserveData
			case didSwipeGear(SwipeAction, Gear.Summary)
		}

		public enum DelegateAction: Equatable {}

		public enum InternalAction: Equatable {
			case gearResponse(TaskResult<[Gear.Summary]>)
			case didLoadEditableGear(TaskResult<Gear.Edit>)
			case didDeleteGear(TaskResult<Gear.Summary>)
			case didRequestDeleteGear(Gear.Summary)

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
			case gearEditor(GearEditor.State)
			case gearList(GearList.State)
			case alert(AlertState<AlertAction>)
		}

		public enum Action: Equatable {
			case gearEditor(GearEditor.Action)
			case gearList(GearList.Action)
			case alert(AlertAction)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.gearEditor, action: /Action.gearEditor) {
				GearEditor()
			}
			Scope(state: /State.gearList, action: /Action.gearList) {
				GearList()
			}
		}
	}

	enum CancelID { case observeGear }

	public enum ErrorID: Hashable {
		case gearNotFound
		case loadingGearFailed
		case failedToDeleteGear
	}

	public enum AlertAction: Equatable {
		case didTapDeleteButton(Gear.Summary)
		case didTapDismissButton
	}

	@Dependency(\.gear) var gear

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didObserveData:
					return observeGear()

				case let .didSwipeGear(action, gear):
					switch action {
					case .edit:
						return .run { [id = gear.id] send in
							await send(.internal(.didLoadEditableGear(TaskResult {
								try await self.gear.edit(id)
							})))
						}

					case .delete:
						state.destination = .alert(Self.alert(toDelete: gear))
						return .none
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .gearResponse(.success(gear)):
					state.recentGear = .init(uniqueElements: gear)
					return .none

				case let .didLoadEditableGear(.success(gear)):
					state.destination = .gearEditor(.init(value: .edit(gear)))
					return .none

				case .didDeleteGear(.success):
					return .none

				case let .didRequestDeleteGear(gear):
					return .run { send in
						await send(.internal(.didDeleteGear(TaskResult {
							try await self.gear.delete(gear.id)
							return gear
						})))
					}

				case let .gearResponse(.failure(error)):
					return state.errors
						.enqueue(.loadingGearFailed, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableGear(.failure(error)):
					return state.errors
						.enqueue(.gearNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didDeleteGear(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteGear, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.alert(.didTapDeleteButton(gear)))):
					state.destination = nil
					return .send(.internal(.didRequestDeleteGear(gear)))

				case let .destination(.presented(.gearEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.gearList(.delegate(delegateAction)))):
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
						.destination(.presented(.gearEditor(.view))),
						.destination(.presented(.gearEditor(.internal))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
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
			case .view(.didSwipeGear(.delete, _)):
				return Analytics.Gear.Deleted()
			default:
				return nil
			}
		}
	}

	private func observeGear() -> Effect<Action> {
		.run { send in
			for try await gear in self.gear.overview() {
				await send(.internal(.gearResponse(.success(gear))))
			}
		} catch: { error, send in
			await send(.internal(.gearResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeGear, cancelInFlight: true)
	}
}

extension GearOverview {
	static func alert(toDelete gear: Gear.Summary) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(gear.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteButton(gear))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}

public struct GearOverviewView: View {
	let store: StoreOf<GearOverview>

	struct ViewState: Equatable {
		let gear: IdentifiedArrayOf<Gear.Summary>

		init(state: GearOverview.State) {
			self.gear = state.recentGear
		}
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Group {
				if viewStore.gear.isEmpty {
					Text(Strings.Gear.Error.Empty.message)
				} else {
					ForEach(viewStore.gear) { gear in
						Gear.View(gear: gear)
							.swipeActions(allowsFullSwipe: true) {
								EditButton { viewStore.send(.didSwipeGear(.edit, gear)) }
								DeleteButton { viewStore.send(.didSwipeGear(.delete, gear)) }
							}
					}
				}
			}
			.task { await viewStore.send(.didObserveData).finish() }
		})
		.errors(store: store.scope(state: \.errors, action: { .internal(.errors($0)) }))
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearOverview.Destination.State.gearList,
			action: GearOverview.Destination.Action.gearList
		) { (store: StoreOf<GearList>) in
			GearListView(store: store)
		}
		.sheet(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /GearOverview.Destination.State.gearEditor,
			action: GearOverview.Destination.Action.gearEditor
		) { (store: StoreOf<GearEditor>) in
			NavigationStack {
				GearEditorView(store: store)
			}
		}
	}
}

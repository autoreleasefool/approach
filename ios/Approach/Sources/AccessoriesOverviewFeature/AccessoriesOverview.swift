import AlleyEditorFeature
import AlleysListFeature
import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import GearEditorFeature
import GearListFeature
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

// swiftlint:disable:next type_body_length
@Reducer
public struct AccessoriesOverview: Reducer {
	static let recentAlleysLimit = 5
	static let recentGearLimit = 10

	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		public var errors: Errors<ErrorID>.State = .init()

		@PresentationState public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case onAppear
			case didObserveData
			case didTapViewAllAlleys
			case didTapViewAllGear
			case didTapGearKind(Gear.Kind)
			case didTapAddAlley
			case didTapAddGear
			case didSwipe(SwipeAction, Item)
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case itemsResponse(TaskResult<[Item]>)
			case didFinishDeletingItem(TaskResult<Item>)
			case didLoadEditableAlley(TaskResult<Alley.EditWithLanes>)
			case didLoadEditableGear(TaskResult<Gear.Edit>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	@Reducer
	public struct Destination: Reducer {
		public enum State: Equatable {
			case alleyEditor(AlleyEditor.State)
			case alleysList(AlleysList.State)
			case gearEditor(GearEditor.State)
			case gearList(GearList.State)
			case alert(AlertState<AlertAction>)
		}

		public enum Action: Equatable {
			case alleyEditor(AlleyEditor.Action)
			case alleysList(AlleysList.Action)
			case gearEditor(GearEditor.Action)
			case gearList(GearList.Action)
			case alert(PresentationAction<AlertAction>)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: /State.alleyEditor, action: /Action.alleyEditor) {
				AlleyEditor()
			}
			Scope(state: /State.alleysList, action: /Action.alleysList) {
				AlleysList()
			}
			Scope(state: /State.gearEditor, action: /Action.gearEditor) {
				GearEditor()
			}
			Scope(state: /State.gearList, action: /Action.gearList) {
				GearList()
			}
		}
	}

	public enum Item: Equatable {
		case alley(Alley.Summary)
		case gear(Gear.Summary)

		var name: String {
			switch self {
			case let .alley(alley): return alley.name
			case let .gear(gear): return gear.name
			}
		}
	}

	public enum SwipeAction: Equatable {
		case edit
//		case delete
	}

	enum CancelID {
		case observeAlleys
		case observeGear
	}

	public enum ErrorID: Hashable {
		case itemNotFound
		case loadingItemsFailed
		case failedToDeleteItem
	}

	public enum AlertAction: Equatable {
		case didTapDeleteItemButton(Item)
		case didTapDismissButton
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.gear) var gear
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: /Action.internal..Action.InternalAction.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didObserveData:
					return .merge(observeAlleys(), observeGear())

//				case let .didSwipe(.delete, item):
//					state.destination = .alert(Self.alert(toDeleteItem: item))
//					return .none

				case let .didSwipe(.edit, item):
					switch item {
					case let .alley(alley):
						return .run { send in
							await send(.internal(.didLoadEditableAlley(TaskResult {
								try await self.alleys.edit(alley.id)
							})))
						}
					case let .gear(gear):
						return .run { send in
							await send(.internal(.didLoadEditableGear(TaskResult {
								try await self.gear.edit(gear.id)
							})))
						}
					}

				case .didTapViewAllGear:
					state.destination = .gearList(.init(kind: nil))
					return .none

				case .didTapViewAllAlleys:
					state.destination = .alleysList(.init())
					return .none

				case .didTapAddAlley:
					state.destination = .alleyEditor(.init(value: .create(.default(withId: uuid()))))
					return .none

				case .didTapAddGear:
					let avatar = Avatar.Summary(id: uuid(), value: .text("", .default))
					state.destination = .gearEditor(.init(value: .create(.default(withId: uuid(), avatar: avatar))))
					return .none

				case let .didTapGearKind(kind):
					state.destination = .gearList(.init(kind: kind))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .itemsResponse(.success(items)):
					switch items.first {
					case .alley:
						state.recentAlleys = .init(uniqueElements: items.compactMap {
							guard case let .alley(alley) = $0 else { return nil }
							return alley
						})
					case .gear:
						state.recentGear = .init(uniqueElements: items.compactMap {
							guard case let .gear(gear) = $0 else { return nil }
							return gear
						})
					case .none:
						break
					}
					return .none

				case let .didLoadEditableAlley(.success(alley)):
					state.destination = .alleyEditor(.init(value: .edit(alley)))
					return .none

				case let .didLoadEditableGear(.success(gear)):
					state.destination = .gearEditor(.init(value: .edit(gear)))
					return .none

				case .didFinishDeletingItem(.success):
					return .none

				case let .itemsResponse(.failure(error)):
					return state.errors
						.enqueue(.loadingItemsFailed, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableAlley(.failure(error)):
					return state.errors
						.enqueue(.itemNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didLoadEditableGear(.failure(error)):
					return state.errors
						.enqueue(.itemNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
						.map { .internal(.errors($0)) }

				case let .didFinishDeletingItem(.failure(error)):
					return state.errors
						.enqueue(.failedToDeleteItem, thrownError: error, toastMessage: Strings.Error.Toast.failedToDelete)
						.map { .internal(.errors($0)) }

				case let .destination(.presented(.alert(.presented(.didTapDeleteItemButton(item))))):
					state.destination = nil
					switch item {
					case let .alley(alley):
						return .run { send in
							await send(.internal(.didFinishDeletingItem(TaskResult {
								try await self.alleys.delete(alley.id)
								return .alley(alley)
							})))
						}
					case let .gear(gear):
						return .run { send in
							await send(.internal(.didFinishDeletingItem(TaskResult {
								try await self.gear.delete(gear.id)
								return .gear(gear)
							})))
						}
					}

				case let .destination(.presented(.alleyEditor(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

				case let .destination(.presented(.alleysList(.delegate(delegateAction)))):
					switch delegateAction {
					case .never:
						return .none
					}

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
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
						.destination(.presented(.alert(.dismiss))),
						.destination(.presented(.alert(.presented(.didTapDismissButton)))),
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
//			case .view(.didSwipe(.delete, .gear)):
//				return Analytics.Gear.Deleted()
//			case .view(.didSwipe(.delete, .alley)):
//				return Analytics.Alley.Deleted()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}

	private func observeAlleys() -> Effect<Action> {
		.run { send in
			for try await alleys in self.alleys.mostRecent(limit: Self.recentAlleysLimit) {
				await send(.internal(.itemsResponse(.success(alleys.map { .alley($0) }))))
			}
		} catch: { error, send in
			await send(.internal(.itemsResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeAlleys)
	}

	private func observeGear() -> Effect<Action> {
		.run { send in
			for try await gear in self.gear.mostRecentlyUsed(limit: Self.recentGearLimit) {
				await send(.internal(.itemsResponse(.success(gear.map { .gear($0) }))))
			}
		} catch: { error, send in
			await send(.internal(.itemsResponse(.failure(error))))
		}
		.cancellable(id: CancelID.observeGear)
	}
}

extension AccessoriesOverview {
	static func alert(toDeleteItem: Item) -> AlertState<AlertAction> {
		.init(
			title: TextState(Strings.Form.Prompt.delete(toDeleteItem.name)),
			primaryButton: .destructive(
				TextState(Strings.Action.delete),
				action: .send(.didTapDeleteItemButton(toDeleteItem))
			),
			secondaryButton: .cancel(
				TextState(Strings.Action.cancel),
				action: .send(.didTapDismissButton)
			)
		)
	}
}

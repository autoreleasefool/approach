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
import SwiftUIExtensionsPackageLibrary

@Reducer
public struct AccessoriesOverview: Reducer, Sendable {
	static let recentAlleysLimit = 5
	static let recentGearLimit = 10

	@ObservableState
	public struct State: Equatable {
		public var recentAlleys: IdentifiedArrayOf<Alley.Summary> = []
		public var recentGear: IdentifiedArrayOf<Gear.Summary> = []

		public var errors = Errors<ErrorID>.State()

		@Presents public var destination: Destination.State?

		public init() {}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case task
			case onAppear
			case didTapViewAllAlleys
			case didTapViewAllGear
			case didTapGearKind(Gear.Kind)
			case didTapAddAlley
			case didTapAddGear
			case didSwipe(SwipeAction, Item)
		}
		@CasePathable public enum Delegate { case doNothing }
		@CasePathable public enum Internal {
			case itemsResponse(Result<[Item], Error>)
			case didFinishDeletingItem(Result<Item, Error>)
			case didLoadEditableAlley(Result<Alley.EditWithLanes, Error>)
			case didLoadEditableGear(Result<Gear.Edit, Error>)

			case errors(Errors<ErrorID>.Action)
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case alleyEditor(AlleyEditor)
		case alleysList(AlleysList)
		case gearEditor(GearEditor)
		case gearList(GearList)
		case alert(AlertState<AlertAction>)
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

	public enum SwipeAction {
		case edit
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

	@Dependency(AlleysRepository.self) var alleys
	@Dependency(\.analytics) var analytics
	@Dependency(GearRepository.self) var gear
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

				case .task:
					return .merge(observeAlleys(), observeGear())

				case let .didSwipe(.edit, item):
					switch item {
					case let .alley(alley):
						return .run { send in
							await send(.internal(.didLoadEditableAlley(Result {
								try await self.alleys.edit(alley.id)
							})))
						}
					case let .gear(gear):
						return .run { send in
							await send(.internal(.didLoadEditableGear(Result {
								try await self.gear.edit(gear.id)
							})))
						}
					}

				case .didTapViewAllGear:
					state.destination = .gearList(GearList.State(kind: nil))
					return .none

				case .didTapViewAllAlleys:
					state.destination = .alleysList(AlleysList.State())
					return .none

				case .didTapAddAlley:
					state.destination = .alleyEditor(AlleyEditor.State(value: .create(.default(withId: uuid()))))
					return .none

				case .didTapAddGear:
					let avatar = Avatar.Summary(id: uuid(), value: .text("", .default))
					state.destination = .gearEditor(GearEditor.State(value: .create(.default(withId: uuid(), avatar: avatar))))
					return .none

				case let .didTapGearKind(kind):
					state.destination = .gearList(GearList.State(kind: kind))
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .itemsResponse(.success(items)):
					switch items.first {
					case .alley:
						state.recentAlleys = IdentifiedArray(uniqueElements: items.compactMap {
							guard case let .alley(alley) = $0 else { return nil }
							return alley
						})
					case .gear:
						state.recentGear = IdentifiedArray(uniqueElements: items.compactMap {
							guard case let .gear(gear) = $0 else { return nil }
							return gear
						})
					case .none:
						break
					}
					return .none

				case let .didLoadEditableAlley(.success(alley)):
					state.destination = .alleyEditor(AlleyEditor.State(value: .edit(alley)))
					return .none

				case let .didLoadEditableGear(.success(gear)):
					state.destination = .gearEditor(GearEditor.State(value: .edit(gear)))
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

				case let .destination(.presented(.alert(.didTapDeleteItemButton(item)))):
					state.destination = nil
					switch item {
					case let .alley(alley):
						return .run { send in
							await send(.internal(.didFinishDeletingItem(Result {
								try await self.alleys.delete(alley.id)
								return .alley(alley)
							})))
						}
					case let .gear(gear):
						return .run { send in
							await send(.internal(.didFinishDeletingItem(Result {
								try await self.gear.delete(gear.id)
								return .gear(gear)
							})))
						}
					}

				case .destination(.dismiss),
						.destination(.presented(.gearEditor(.view))),
						.destination(.presented(.gearEditor(.internal))),
						.destination(.presented(.gearEditor(.binding))),
						.destination(.presented(.gearEditor(.delegate(.doNothing)))),
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleyEditor(.binding))),
						.destination(.presented(.alleyEditor(.delegate(.doNothing)))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.alleysList(.delegate(.doNothing)))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
						.destination(.presented(.gearList(.delegate(.doNothing)))),
						.destination(.presented(.alert(.didTapDismissButton))),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)

		AnalyticsReducer<State, Action> { _, action in
			switch action {
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

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didFinishDeletingItem(.failure(error))),
				let .internal(.didLoadEditableGear(.failure(error))),
				let .internal(.didLoadEditableAlley(.failure(error))),
				let .internal(.itemsResponse(.failure(error))):
				return error
			default:
				return nil
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
		AlertState(
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

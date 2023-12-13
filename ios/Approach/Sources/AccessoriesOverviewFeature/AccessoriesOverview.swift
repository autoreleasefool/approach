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

@Reducer
// swiftlint:disable:next type_body_length
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

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction {
			case task
			case onAppear
			case didTapViewAllAlleys
			case didTapViewAllGear
			case didTapGearKind(Gear.Kind)
			case didTapAddAlley
			case didTapAddGear
			case didSwipe(SwipeAction, Item)
		}
		@CasePathable public enum DelegateAction { case doNothing }
		@CasePathable public enum InternalAction {
			case itemsResponse(Result<[Item], Error>)
			case didFinishDeletingItem(Result<Item, Error>)
			case didLoadEditableAlley(Result<Alley.EditWithLanes, Error>)
			case didLoadEditableGear(Result<Gear.Edit, Error>)

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

		public enum Action {
			case alleyEditor(AlleyEditor.Action)
			case alleysList(AlleysList.Action)
			case gearEditor(GearEditor.Action)
			case gearList(GearList.Action)
			case alert(PresentationAction<AlertAction>)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.alleyEditor, action: \.alleyEditor) {
				AlleyEditor()
			}
			Scope(state: \.alleysList, action: \.alleysList) {
				AlleysList()
			}
			Scope(state: \.gearEditor, action: \.gearEditor) {
				GearEditor()
			}
			Scope(state: \.gearList, action: \.gearList) {
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

	@Dependency(\.alleys) var alleys
	@Dependency(\.gear) var gear
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
						.destination(.presented(.gearEditor(.delegate(.doNothing)))),
						.destination(.presented(.alleyEditor(.view))),
						.destination(.presented(.alleyEditor(.internal))),
						.destination(.presented(.alleyEditor(.delegate(.doNothing)))),
						.destination(.presented(.alleysList(.internal))),
						.destination(.presented(.alleysList(.view))),
						.destination(.presented(.alleysList(.delegate(.doNothing)))),
						.destination(.presented(.gearList(.internal))),
						.destination(.presented(.gearList(.view))),
						.destination(.presented(.gearList(.delegate(.doNothing)))),
						.destination(.presented(.alert(.dismiss))),
						.destination(.presented(.alert(.presented(.didTapDismissButton)))),
						.errors(.internal), .errors(.view), .errors(.delegate(.doNothing)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

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

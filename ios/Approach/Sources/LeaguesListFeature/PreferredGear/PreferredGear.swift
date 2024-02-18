import AvatarServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import GearRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct PreferredGear: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let bowler: Bowler.ID

		public var gear: IdentifiedArrayOf<Gear.Summary> = []

		@Presents var gearPicker: ResourcePicker<Gear.Summary, Bowler.ID>.State?
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didFirstAppear
			case didTapManageButton
		}
		@CasePathable public enum Delegate {
			case errorLoadingGear(Result<Never, Error>)
			case errorUpdatingPreferredGear(Result<Never, Error>)
		}
		@CasePathable public enum Internal {
			case didLoadGear(Result<[Gear.Summary], Error>)
			case didUpdatePreferredGear(Result<[Gear.Summary], Error>)
			case gearPicker(PresentationAction<ResourcePicker<Gear.Summary, Bowler.ID>.Action>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.gear) var gear

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didFirstAppear:
					return .run { [bowler = state.bowler] send in
						await send(.internal(.didLoadGear(Result {
							try await gear.preferredGear(forBowler: bowler)
						})))
					}

				case .didTapManageButton:
					state.gearPicker = .init(
						selected: Set(state.gear.ids),
						query: state.bowler,
						limit: 0,
						showsCancelHeaderButton: false
					)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didUpdatePreferredGear(.success):
					return .none

				case let .didLoadGear(.success(gear)):
					state.gear = .init(uniqueElements: gear)
					return .none

				case let .didLoadGear(.failure(error)):
					return .send(.delegate(.errorLoadingGear(.failure(error))))

				case let .didUpdatePreferredGear(.failure(error)):
					return .send(.delegate(.errorUpdatingPreferredGear(.failure(error))))

				case let .gearPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(gear):
						state.gear = .init(uniqueElements: gear)
						return .run { [gear = state.gear, bowler = state.bowler] send in
							await send(.internal(.didUpdatePreferredGear(Result {
								try await self.gear.updatePreferredGear(gear.map(\.id), forBowler: bowler)
								return Array(gear)
							})))
						}
					}

				case .gearPicker(.dismiss), .gearPicker(.presented(.view)), .gearPicker(.presented(.internal)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$gearPicker, action: \.internal.gearPicker) {
			ResourcePicker { _ in gear.list(ordered: .byName) }
		}
	}
}

@ViewAction(for: PreferredGear.self)
public struct PreferredGearView: View {
	@Perception.Bindable public var store: StoreOf<PreferredGear>

	public init(store: StoreOf<PreferredGear>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			Section {
				if store.gear.isEmpty {
					Text(Strings.Bowler.List.PreferredGear.footer)
				} else {
					ForEach(store.gear) { gear in
						Gear.ViewWithAvatar(gear)
					}
				}
			} header: {
				HStack(alignment: .firstTextBaseline) {
					Text(Strings.Bowler.List.preferredGear)
					Spacer()
					Button { send(.didTapManageButton) } label: {
						Text(Strings.Action.manage)
							.font(.caption)
					}
				}
				Text(Strings.Bowler.List.preferredGear)
			} footer: {
				if store.gear.isEmpty {
					EmptyView()
				} else {
					Text(Strings.Bowler.List.PreferredGear.footer)
				}
			}
			.onFirstAppear { send(.didFirstAppear) }
			.navigationDestinationWrapper(
				item: $store.scope(state: \.gearPicker, action: \.internal.gearPicker)
			) { store in
				ResourcePickerView(store: store) {
					Gear.ViewWithAvatar($0)
				}
			}
		}
	}
}

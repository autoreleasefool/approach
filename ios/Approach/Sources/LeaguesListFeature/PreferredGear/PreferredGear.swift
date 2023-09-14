import AvatarServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import GearRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI

public struct PreferredGear: Reducer {
	public struct State: Equatable {
		public let bowler: Bowler.ID

		public var gear: IdentifiedArrayOf<Gear.Summary> = []

		@PresentationState var gearPicker: ResourcePicker<Gear.Summary, Bowler.ID>.State?
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didFirstAppear
			case didTapManageButton
		}
		public enum DelegateAction: Equatable {
			case errorLoadingGear(TaskResult<Never>)
			case errorUpdatingPreferredGear(TaskResult<Never>)
		}
		public enum InternalAction: Equatable {
			case didLoadGear(TaskResult<[Gear.Summary]>)
			case didUpdatePreferredGear(TaskResult<[Gear.Summary]>)
			case gearPicker(PresentationAction<ResourcePicker<Gear.Summary, Bowler.ID>.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
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
						await send(.internal(.didLoadGear(TaskResult {
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
							await send(.internal(.didUpdatePreferredGear(TaskResult {
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
		.ifLet(\.$gearPicker, action: /Action.internal..Action.InternalAction.gearPicker) {
			ResourcePicker { _ in gear.list(ordered: .byName) }
		}
	}
}

public struct PreferredGearView: View {
	let store: StoreOf<PreferredGear>

	public init(store: StoreOf<PreferredGear>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			Section {
				if viewStore.gear.isEmpty {
					Text(Strings.Bowler.List.PreferredGear.footer)
				} else {
					ForEach(viewStore.gear) { gear in
						Gear.ViewWithAvatar(gear)
					}
				}
			} header: {
				HStack(alignment: .firstTextBaseline) {
					Text(Strings.Bowler.List.preferredGear)
					Spacer()
					Button { viewStore.send(.didTapManageButton) } label: {
						Text(Strings.Action.manage)
							.font(.caption)
					}
				}
				Text(Strings.Bowler.List.preferredGear)
			} footer: {
				if viewStore.gear.isEmpty {
					EmptyView()
				} else {
					Text(Strings.Bowler.List.PreferredGear.footer)
				}
			}
			.onFirstAppear { viewStore.send(.didFirstAppear) }
		})
		.navigationDestination(
			store: store.scope(state: \.$gearPicker, action: { .internal(.gearPicker($0)) })
		) { store in
			ResourcePickerView(store: store) {
				Gear.ViewWithAvatar($0)
			}
		}
	}
}

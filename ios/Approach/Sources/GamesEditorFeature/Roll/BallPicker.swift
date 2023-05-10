import ComposableArchitecture
import GearRepositoryInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct BallPicker: Reducer {
	public struct State: Equatable {
		public var forBowler: Bowler.ID
		public var selected: Gear.ID?
		public var bowlingBalls: IdentifiedArrayOf<Gear.Summary> = []

		public var selectedBall: Gear.Summary? {
			guard let selected else { return nil }
			return bowlingBalls[id: selected]
		}

		init(forBowler: Bowler.ID, selected: Gear.ID?) {
			self.forBowler = forBowler
			self.selected = selected
		}
	}

	public enum Action: Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didTapGear(Gear.ID)
			case didTapCancelButton
		}
		public enum DelegateAction: Equatable {
			case didFinish
		}
		public enum InternalAction: Equatable {
			case didLoadBowlingBalls(TaskResult<[Gear.Summary]>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	struct CancelObservationID {}

	init() {}

	@Dependency(\.gear) var gear

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .run { [bowler = state.forBowler] send in
						for try await bowlingBalls in gear.list(ownedBy: bowler, ofKind: .bowlingBall, ordered: .byRecentlyUsed) {
							await send(.internal(.didLoadBowlingBalls(.success(bowlingBalls))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadBowlingBalls(.failure(error))))
					}
					.cancellable(id: CancelObservationID.self, cancelInFlight: true)

				case let .didTapGear(id):
					state.selected = id
					return .task { .delegate(.didFinish) }

				case .didTapCancelButton:
					return .task { .delegate(.didFinish) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadBowlingBalls(.success(bowlingBalls)):
					state.bowlingBalls = .init(uniqueElements: bowlingBalls)
					return .none

				case .didLoadBowlingBalls(.failure):
					// TODO: handle failure to load bowling balls
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct BallPickerView: View {
	let store: StoreOf<BallPicker>

	enum ViewAction {
		case didAppear
		case didTapGear(Gear.ID)
		case didTapCancelButton
	}

	init(store: StoreOf<BallPicker>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: BallPicker.Action.init, content: { viewStore in
			List(viewStore.bowlingBalls) { bowlingBall in
				Button { viewStore.send(.didTapGear(bowlingBall.id)) } label: {
					Label(
						bowlingBall.name,
						systemImage: viewStore.selected == bowlingBall.id ? "smallcircle.filled.circle" : "circle"
					)
					.foregroundColor(.appAction)
				}
				.buttonStyle(TappableElement())
			}
			.navigationTitle(Strings.BowlingBall.List.title)
			.navigationBarTitleDisplayMode(.inline)
			.toolbar {
				ToolbarItem(placement: .navigationBarLeading) {
					Button(Strings.Action.cancel) { viewStore.send(.didTapCancelButton) }
				}
			}
			.onAppear { viewStore.send(.didAppear) }
		})
	}
}

extension BallPicker.Action {
	init(action: BallPickerView.ViewAction) {
		switch action {
		case .didAppear:
			self = .view(.didAppear)
		case let .didTapGear(id):
			self = .view(.didTapGear(id))
		case .didTapCancelButton:
			self = .view(.didTapCancelButton)
		}
	}
}

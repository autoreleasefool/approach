import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FeatureFlagsServiceInterface
import ModelsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct RollEditor: Reducer {
	public struct State: Equatable {
		public var ballRolled: Gear.Named?
		public var didFoul: Bool
		public var isGearEnabled: Bool

		init(ballRolled: Gear.Named?, didFoul: Bool) {
			self.ballRolled = ballRolled
			self.didFoul = didFoul

			@Dependency(\.featureFlags) var featureFlags
			self.isGearEnabled = featureFlags.isEnabled(.gear)
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didToggleFoul
		}
		public enum InternalAction: Equatable {}
		public enum DelegateAction: Equatable {
			case didEditRoll
			case didTapBall
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didToggleFoul:
					state.didFoul.toggle()
					return .send(.delegate(.didEditRoll))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

// MARK: - View

public struct RollEditorView: View {
	let store: StoreOf<RollEditor>

	enum ViewAction {
		case didTapBall
		case didToggleFoul
	}

	init(store: StoreOf<RollEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: RollEditor.Action.init, content: { viewStore in
			HStack(alignment: .bottom) {
				if viewStore.isGearEnabled {
					Button { viewStore.send(.didTapBall) } label: {
						VStack(alignment: .leading, spacing: .tinySpacing) {
							Text(Strings.Roll.Properties.Ball.title)
								.font(.caption)
								.italic()
								.foregroundColor(.white)
							Text(viewStore.ballRolled?.name ?? Strings.Roll.Properties.Ball.noneSelected)
								.foregroundColor(.white)
						}
					}
					.buttonStyle(TappableElement())
				}

				Spacer()

				Button { viewStore.send(.didToggleFoul) } label: {
					HStack(spacing: .smallSpacing) {
						Text(Strings.Roll.Properties.Foul.title)
							.foregroundColor(viewStore.didFoul ? .appError : .white)
						Image(systemName: viewStore.didFoul ? "f.cursive.circle.fill" : "f.cursive.circle")
							.resizable()
							.frame(width: .smallIcon, height: .smallIcon)
							.foregroundColor(viewStore.didFoul ? .appError : .white)
					}
				}
				.buttonStyle(TappableElement())
			}
		})
	}
}

extension RollEditor.Action {
	init(action: RollEditorView.ViewAction) {
		switch action {
		case .didToggleFoul:
			self = .view(.didToggleFoul)
		case .didTapBall:
			self = .delegate(.didTapBall)
		}
	}
}

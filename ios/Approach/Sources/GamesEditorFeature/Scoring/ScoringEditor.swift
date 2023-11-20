import AnalyticsServiceInterface
import ComposableArchitecture
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct ScoringEditor: Reducer {
	public struct State: Equatable {
		public var scoringMethod: Game.ScoringMethod
		@BindingState public var score: Int
	}

	public enum Action: Equatable {
		public enum ViewAction: BindableAction, Equatable {
			case onAppear
			case toggleManualScoring(Bool)
			case binding(BindingAction<State>)
		}
		public enum DelegateAction: Equatable {
			case didSetManualScore(Int)
			case didClearManualScore
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	enum CancelID { case manualScore }

	@Dependency(\.continuousClock) var clock

	public var body: some ReducerOf<Self> {
		BindingReducer(action: /Action.view)

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case let .toggleManualScoring(isManual):
					state.scoringMethod = isManual ? .manual : .byFrame
					switch state.scoringMethod {
					case .byFrame:
						return .merge(
							.cancel(id: CancelID.manualScore),
							.send(.delegate(.didClearManualScore))
						)

					case .manual:
						return .send(.delegate(.didSetManualScore(state.score)))
					}

				case .binding(\.$score):
					state.score = min(max(state.score, 0), Game.MAXIMUM_SCORE)
					return .run { [score = state.score] send in
						try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
						await send(.delegate(.didSetManualScore(score)))
					}.cancellable(id: CancelID.manualScore)

				case .binding:
					return .none
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

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

public struct ScoringEditorView: View {
	let store: StoreOf<ScoringEditor>

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			List {
				Section {
					Toggle(
						Strings.Scoring.Editor.Fields.ManualScore.title,
						isOn: viewStore.binding(get: { $0.scoringMethod == .manual }, send: { .toggleManualScoring($0) })
					)
					.toggleStyle(CheckboxToggleStyle())
				} footer: {
					Text(Strings.Scoring.Editor.Fields.ManualScore.help)
				}

				if viewStore.scoringMethod == .manual {
					Section {
						TextField(
							Strings.Scoring.Editor.Fields.ManualScore.label,
							value: viewStore.$score,
							formatter: NumberFormatter()
						)
						.keyboardType(.numberPad)
					}
				}
			}
			.navigationTitle(Strings.Scoring.title)
			.onAppear { viewStore.send(.onAppear) }
		})
	}
}

#if DEBUG
struct ScoringEditorPreview: PreviewProvider {
	static var previews: some View {
		ScoringEditorView(store: .init(
			initialState: .init(scoringMethod: .byFrame, score: 120),
			reducer: ScoringEditor.init
		))
	}
}
#endif

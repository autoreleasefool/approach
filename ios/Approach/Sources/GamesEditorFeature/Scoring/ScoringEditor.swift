import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

@Reducer
public struct ScoringEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var scoringMethod: Game.ScoringMethod
		public var score: Int

		var isManualScoring: Bool {
			scoringMethod == .manual
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case toggleManualScoring(Bool)
		}
		@CasePathable public enum Delegate {
			case didSetManualScore(Int)
			case didClearManualScore
		}
		@CasePathable public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	enum CancelID { case manualScore }

	@Dependency(\.continuousClock) var clock

	public var body: some ReducerOf<Self> {
		BindingReducer()

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
				}

			case .internal(.doNothing):
				return .none

			case .binding(\.score):
				state.score = min(max(state.score, 0), Game.MAXIMUM_SCORE)
				return .run { [score = state.score] send in
					try await clock.sleep(for: .nanoseconds(NSEC_PER_SEC / 3))
					await send(.delegate(.didSetManualScore(score)))
				}.cancellable(id: CancelID.manualScore)

			case .delegate, .binding:
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

@ViewAction(for: ScoringEditor.self)
public struct ScoringEditorView: View {
	@Bindable public var store: StoreOf<ScoringEditor>

	public var body: some View {
		List {
			Section {
				Toggle(
					Strings.Scoring.Editor.Fields.ManualScore.title,
					isOn: $store.isManualScoring.sending(\.view.toggleManualScoring)
				)
				.toggleStyle(CheckboxToggleStyle())
			} footer: {
				Text(Strings.Scoring.Editor.Fields.ManualScore.help)
			}

			if store.scoringMethod == .manual {
				Section {
					TextField(
						Strings.Scoring.Editor.Fields.ManualScore.label,
						value: $store.score,
						formatter: NumberFormatter()
					)
					.keyboardType(.numberPad)
				}
			}
		}
		.navigationTitle(Strings.Scoring.title)
		.onAppear { send(.onAppear) }
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

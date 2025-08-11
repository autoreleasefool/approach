import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FramesRepositoryInterface
import GamesRepositoryInterface
import ModelsLibrary
import ScoreSheetLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ToastLibrary

@Reducer
public struct GamesEditorNext: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		// IDs for games being edited (and their corresponding bowlers)
		@Shared(.bowlerIds) public var bowlerIds: [Bowler.ID]
		@Shared(.bowlerGameIds) public var bowlerGameIds: [Bowler.ID: [Game.ID]]

		// ID details for the current entity being edited
		@Shared(.currentBowlerId) public var currentBowlerId: Bowler.ID
		@Shared(.currentGameId) public var currentGameId: Game.ID
		@Shared(.currentFrame) public var currentFrame: Frame.Selection
		@Shared(.game) public var game: Game.Edit?
		@Shared(.frames) public var frames: [Frame.Edit]?
		@Shared(.score) public var score: ScoredGame?

		@Shared(.gameLastLoadedAt) public var gameLastLoadedAt: GameLoadDate?

		// Sizing
		public var sheetDetent: PresentationDetent = .height(40)
		public var gameDetailsHeaderSize: CGSize = .zero
		public var gameDetailsMinimumContentSize: CGSize = .zero
		public var safeAreaInsets = EdgeInsets()
		public var headerContentSize: CGSize = .zero
		public var rollEditorSize: CGSize = .zero
		public var frameContentSize: CGSize = .zero
		public var sheetContentSize: CGSize = .zero
		public var windowContentSize: CGSize = .zero

		public var isScoreSheetVisible = true

		public var shouldRequestAppStoreReview: Bool = false

		public var didPromptLaneDuplication = false
		public var willShowDuplicateLanesAlert = false

		public var frameEditor = FrameEditorNext.State()
		public var rollEditor = RollEditorNext.State()
		public var gamesHeader = GamesHeaderNext.State()

		public var stateLoader = GamesEditorStateLoading.State()
		public var stateSerializer = GamesEditorStateSerialization.State()
		public var stateValidator = GamesEditorStateValidation.State()

		@Presents public var destination: Destination.State?
		@Presents public var toast: ToastState<ToastAction>?
		public var errors = Errors<ErrorID>.State()

		public init(
			bowlerIds: [Bowler.ID],
			bowlerGameIds: [Bowler.ID: [Game.ID]],
			initialBowlerId: Bowler.ID?,
			initialGameId: Game.ID?
		) {
			precondition(bowlerGameIds.allSatisfy { $0.value.count == bowlerGameIds.first!.value.count })

			self.$bowlerIds.withLock { $0 = bowlerIds }
			self.$bowlerGameIds.withLock { $0 = bowlerGameIds }

			let currentBowlerId = initialBowlerId ?? bowlerIds.first!
			let currentGameId = initialGameId ?? bowlerGameIds[currentBowlerId]!.first!
			self.$currentBowlerId.withLock { $0 = currentBowlerId }
			self.$currentGameId.withLock { $0 = currentGameId }

			self.$game.withLock { $0 = nil }
			self.$frames.withLock { $0 = nil }
			self.$score.withLock { $0 = nil }
			self.$gameLastLoadedAt.withLock { $0 = nil }

			// TODO: remove
			self.$currentFrame.withLock { $0 = .init(frameIndex: 0, rollIndex: 0) }
		}

		var measuredBackdropSize: CGSize {
			let sheetContentSize = sheetDetent == .large ? .zero : sheetContentSize
			return .init(
				width: windowContentSize.width,
				height: max(
					windowContentSize.height
						- sheetContentSize.height
						- headerContentSize.height
						- safeAreaInsets.bottom
						- CGFloat.largeSpacing,
					0
				)
			)
		}

		var backdropImageHeight: CGFloat {
			max(
				measuredBackdropSize.height
					- (isScoreSheetVisible ? frameContentSize.height : 0)
					- headerContentSize.height
					+ rollEditorSize.height,
				0
			)
		}

		var currentGameIndex: Int {
			bowlerGameIds[currentBowlerId]?.firstIndex(of: currentGameId) ?? 0
		}

		var manualScore: Int? {
			switch game?.scoringMethod {
			case .byFrame, .none:
				nil
			case .manual:
				game?.score

			}
		}

		fileprivate mutating func showLaneDuplicationPromptIfNecessary() {
			let hasOtherGames = Set(bowlerGameIds.flatMap { $0.value }).count > 1
			if hasOtherGames && !didPromptLaneDuplication {
				didPromptLaneDuplication = true
				willShowDuplicateLanesAlert = true
				destination = nil
			}
		}

		fileprivate mutating func presentLockedAlert() {
			toast = .locked
		}

		fileprivate mutating func presentStrikeOutAlert(withFinalScore: Int) {
			toast = .strikeOut(withFinalScore: withFinalScore)
		}

		fileprivate mutating func lockGameIfFinished() {
			if Frame.isLast(currentFrame.frameIndex) && Frame.isLastRoll(currentFrame.rollIndex) {
				$game.withLock { $0?.locked = .locked }
			}
		}
	}

	public enum Action: ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case onAppear
			case onFirstAppear
			case didChangeSafeAreaInsets(EdgeInsets)
			case didDismissGameDetails
			case didDismissOpenSheet
			case didRequestReview
		}
		@CasePathable
		public enum Internal {
			case adjustBackdrop
			case didDuplicateLanes(Result<Never, Error>)
			case showDuplicateLanesAlert
			case didCalculateHighestScorePossible(Result<Int, Error>)

			case frameEditor(FrameEditorNext.Action)
			case rollEditor(RollEditorNext.Action)
			case gamesHeader(GamesHeaderNext.Action)
			case stateLoader(GamesEditorStateLoading.Action)
			case stateSerializer(GamesEditorStateSerialization.Action)
			case stateValidator(GamesEditorStateValidation.Action)

			case errors(Errors<ErrorID>.Action)
			case toast(PresentationAction<ToastAction>)
			case destination(PresentationAction<Destination.Action>)
		}
		@CasePathable
		public enum Delegate { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ToastAction: Equatable, ToastableAction {
		case unlockGame
		case didDismiss
		case didFinishDismissing
	}

	public enum ErrorID: Hashable, Sendable {
		case failedToSaveLanes
		case failedToCalculateHighestScore
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case gameDetails(GameDetailsNext.State)
			case duplicateLanesAlert(AlertState<DuplicateLanesAlertAction>)
		}

		public enum Action {
			case gameDetails(GameDetailsNext.Action)
			case duplicateLanesAlert(DuplicateLanesAlertAction)
		}

		public enum DuplicateLanesAlertAction: Equatable {
			case confirmDuplicateLanes
			case didTapDismissButton
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.gameDetails, action: \.gameDetails) {
				GameDetailsNext()
			}
		}
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.dismiss) var dismiss
	@Dependency(GamesRepository.self) var games
	@Dependency(\.storeReview) var storeReview

	public var body: some ReducerOf<Self> {
		BindingReducer()

		// We explicitly handle this action before all others so that we can guarantee we are on a valid frame/roll
		Reduce<State, Action> { state, action in
			return .none
//			guard case .binding(\.currentFrame) = action else { return .none }
//			state.setCurrent(rollIndex: state.currentFrame.rollIndex, frameIndex: state.currentFrame.frameIndex)
//			let currentFrameIndex = state.currentFrameIndex
//			let currentRollIndex = state.currentRollIndex
//			state.populateFrames(upTo: currentFrameIndex)
//			state.frames?[currentFrameIndex].guaranteeRollExists(upTo: currentRollIndex)
//			state.syncFrameEditorSharedState()
//			state.syncRollEditorSharedState()
//			return save(frame: state.frames?[currentFrameIndex])
		}

		Scope(state: \.stateValidator, action: \.internal.stateValidator) {
			GamesEditorStateValidation()
		}

		Scope(state: \.stateLoader, action: \.internal.stateLoader) {
			GamesEditorStateLoading()
		}

		Scope(state: \.stateSerializer, action: \.internal.stateSerializer) {
			GamesEditorStateSerialization()
		}

		Scope(state: \.gamesHeader, action: \.internal.gamesHeader) {
			GamesHeaderNext()
		}

		Scope(state: \.frameEditor, action: \.internal.frameEditor) {
			FrameEditorNext()
		}

		Scope(state: \.rollEditor, action: \.internal.rollEditor) {
			RollEditorNext()
		}

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .onFirstAppear:
					return .merge(
						state.stateLoader.initialize()
							.map { .internal(.stateLoader($0)) },
						state.stateSerializer.initialize()
							.map { .internal(.stateSerializer($0)) },
						state.stateValidator.initialize()
							.map { .internal(.stateValidator($0)) }
					)

				case .didRequestReview:
					state.shouldRequestAppStoreReview = false
					return .run { _ in storeReview.didRequestReview() }

				case let .didChangeSafeAreaInsets(newInsets):
					state.safeAreaInsets = newInsets
					return .send(.internal(.adjustBackdrop), animation: .default)

				case .didDismissOpenSheet:
					if let game = state.game {
						state.sheetDetent = .medium
						state.destination = .gameDetails(.init())
					}
					return .send(.internal(.adjustBackdrop), animation: .default)

				case .didDismissGameDetails:
					if state.willShowDuplicateLanesAlert {
						state.willShowDuplicateLanesAlert = false
						return .run { send in
							try await clock.sleep(for: .milliseconds(25))
							await send(.internal(.showDuplicateLanesAlert))
						}
					} else {
						return .none
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .adjustBackdrop:
					switch state.sheetDetent {
					case .large, .medium:
						state.isScoreSheetVisible = false
					default:
						state.isScoreSheetVisible = true
					}
					return .none

				case let .didCalculateHighestScorePossible(.success(highestScore)):
					state.presentStrikeOutAlert(withFinalScore: highestScore)
					return .none

				case let .didCalculateHighestScorePossible(.failure(error)):
					return state.errors
						.enqueue(.failedToCalculateHighestScore, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case .showDuplicateLanesAlert:
					state.destination = .duplicateLanesAlert(.duplicateLanes)
					return .none

				case let .destination(.presented(.duplicateLanesAlert(alertAction))):
					switch alertAction {
					case .confirmDuplicateLanes:
						let currentGame = state.currentGameId
						let otherGames = state.bowlerGameIds.flatMap { $0.value }.filter { $0 != currentGame }
						return .run { _ in
							try await games.duplicateLanes(from: currentGame, toAllGames: otherGames)
						} catch: { error, send in
							await send(.internal(.didDuplicateLanes(.failure(error))))
						}

					case .didTapDismissButton:
						state.destination = .gameDetails(.init())
						return .none
					}

				case let .didDuplicateLanes(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveLanes, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case .gamesHeader(.delegate(.didCloseEditor)):
					return .run { _ in await dismiss() }

				case .gamesHeader(.delegate(.didOpenSettings)):
					// TODO: show settings
					return .none

				case .gamesHeader(.delegate(.didShareGame)):
					// TODO: share game
					return .none

				case let .stateLoader(.delegate(.changeCurrentFrame(frame))):
					return state.stateValidator.updateActiveIndices(
						rollIndex: frame.rollIndex,
						frameIndex: frame.frameIndex
					).map { .internal(.stateValidator($0)) }

				case let .frameEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .didProvokeLock:
						state.presentLockedAlert()
						return .none
					}

				case let .rollEditor(.delegate(delegateAction)):
					switch delegateAction {
					case .didProvokeLock:
						state.presentLockedAlert()
						return .none

					case .didRequestBallPicker:
						// TODO: show ball picker
						return .none
					}

				case let .destination(.presented(.gameDetails(.delegate(delegateAction)))):
					switch delegateAction {
					case .didSelectLanes:
						state.showLaneDuplicationPromptIfNecessary()
						return .none

					case .didProvokeLock:
						state.presentLockedAlert()
						return .none

					case let .didMeasureMinimumSheetContentSize(size):
						state.gameDetailsMinimumContentSize = size
						if state.sheetDetent == .height(.zero) {
							state.sheetDetent = .height(state.gameDetailsMinimumContentSize.height + 40)
						}
						return .none

					case let .didMeasureSectionHeaderContentSize(size):
						state.gameDetailsHeaderSize = size
						return .none

					case .didTapStrikeOut:
						// TODO: show strike out
//						return .run { [gameId = state.currentGameId] send in
//							await send(.internal(.didCalculateHighestScorePossible(Result {
//								try await self.scores.highestScorePossible(gameId)
//							})))
//						}
						return .none

					case let .didProceed(next):
						switch next {
						case let .bowler(_, id):
							state.lockGameIfFinished()
							let gameIndex = state.currentGameIndex
							return state.stateValidator.updateActiveIndices(
								gameId: state.bowlerGameIds[id]![gameIndex],
								bowlerId: id
							).map { .internal(.stateValidator($0)) }

						case let .frame(frameIndex):
							// TODO: guaranteeRollExists + saveFrame?
							return state.stateValidator.updateActiveIndices(
								rollIndex: 0,
								frameIndex: frameIndex
							).map { .internal(.stateValidator($0)) }

						case let .roll(rollIndex):
							// TODO: guaranteeRollExists + saveFrame?
							return state.stateValidator.updateActiveIndices(
								rollIndex: rollIndex
							).map { .internal(.stateValidator($0)) }

						case let .game(_, bowler, game):
							// TODO: state.shouldRequestAppStoreReview
							state.lockGameIfFinished()
							return state.stateValidator.updateActiveIndices(
								gameId: game,
								bowlerId: bowler
							).map { .internal(.stateValidator($0)) }
						}
					}
					

				case let .toast(.presented(toastAction)):
					switch toastAction {
					case .unlockGame:
						state.$game.withLock { $0?.locked.toNext() }
						state.toast = nil
						return .none

					case .didDismiss, .didFinishDismissing:
						state.toast = nil
						return .none
					}

				case .toast(.dismiss):
					state.toast = nil
					return .none

				case .destination(.dismiss),
					.destination(.presented(.gameDetails(.internal))),
					.destination(.presented(.gameDetails(.view))),
					.errors(.delegate(.doNothing)), .errors(.internal), .errors(.view),
					.gamesHeader(.internal), .gamesHeader(.view),
					.frameEditor(.internal), .frameEditor(.view),
					.rollEditor(.internal), .rollEditor(.view),
					.stateLoader(.internal), .stateLoader(.view),
					.stateSerializer(.delegate(.doNothing)), .stateSerializer(.internal), .stateSerializer(.view),
					.stateValidator(.delegate(.doNothing)), .stateValidator(.internal), .stateValidator(.view):
					return .none
				}

			case .binding(\.sheetDetent):
				return .run { send in
					try await clock.sleep(for: .milliseconds(25))
					await send(.internal(.adjustBackdrop), animation: .default)
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}
		.ifLet(\.$toast, action: \.internal.toast) {}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didDuplicateLanes(.failure(error))),
				let .internal(.didCalculateHighestScorePossible(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

@ViewAction(for: GamesEditorNext.self)
public struct GamesEditorNextView: View {
	@Bindable public var store: StoreOf<GamesEditorNext>

	@Environment(\.continuousClock) private var clock
	@Environment(\.safeAreaInsetsProvider) private var safeAreaInsetsProvider
	@Environment(\.requestReview) private var requestReview

	public init(store: StoreOf<GamesEditorNext>) {
		self.store = store
	}

	public var body: some View {
		VStack {
			GamesHeaderNextView(store: store.scope(state: \.gamesHeader, action: \.internal.gamesHeader))
				.measure(key: HeaderContentSizeKey.self, to: $store.headerContentSize)

			VStack {
				if let manualScore = store.manualScore {
					Spacer()

					VStack {
						Text(String(manualScore))
							.font(.largeTitle)
						Text(Strings.Game.Editor.Fields.ManualScore.caption)
							.font(.caption)
					}
					.padding()
					.background(.regularMaterial, in: RoundedRectangle(cornerRadius: .standardRadius, style: .continuous))
					.padding()

					Spacer()
				} else {
					FrameEditorNextView(store: store.scope(state: \.frameEditor, action: \.internal.frameEditor))
						.padding(.top)

					RollEditorNextView(store: store.scope(state: \.rollEditor, action: \.internal.rollEditor))
						.measure(key: RollEditorSizeKey.self, to: $store.rollEditorSize)
						.padding(.horizontal)

					if store.isScoreSheetVisible {
						scoreSheet
							.padding(.top)
							.padding(.horizontal)
							.measure(key: FrameContentSizeKey.self, to: $store.frameContentSize)
					}
				}
			}
			.frame(idealWidth: store.measuredBackdropSize.width, maxHeight: store.measuredBackdropSize.height)

			Spacer()
		}
		.measure(key: WindowContentSizeKey.self, to: $store.windowContentSize)
		.onChange(of: safeAreaInsetsProvider.get()) { send(.didChangeSafeAreaInsets(safeAreaInsetsProvider.get())) }
		.background(alignment: .top) {
			VStack(spacing: 0) {
				Asset.Media.Lane.galaxy.swiftUIImage
					.resizable()
					.scaledToFill()
				Asset.Media.Lane.wood.swiftUIImage
					.resizable()
					.scaledToFill()
			}
			.frame(width: store.measuredBackdropSize.width, height: store.backdropImageHeight)
			.faded()
			.clipped()
			.padding(.top, store.headerContentSize.height)
		}
		.background(Color.black)
		.toolbar(.hidden, for: .tabBar, .navigationBar)
		.onAppear { send(.onAppear) }
		.onFirstAppear { send(.onFirstAppear) }
	}

	@ViewBuilder private var scoreSheet: some View {
		if let game = store.score {
			// TODO: remove unselected
			ScoreSheetScrollView(game: game, configuration: .plain, selection: .constant(.unselected))
		}
	}
}

private struct SheetContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct WindowContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct HeaderContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct FrameContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
private struct RollEditorSizeKey: PreferenceKey, CGSizePreferenceKey {}

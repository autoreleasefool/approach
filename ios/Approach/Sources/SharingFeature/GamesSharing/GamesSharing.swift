import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ComposableExtensionsLibrary
import ErrorsFeature
import FeatureActionLibrary
import GamesRepositoryInterface
import ModelsLibrary
import ScoresRepositoryInterface
import SeriesRepositoryInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct GamesSharing: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let seriesId: Series.ID

		public var series: Series.Shareable?
		public var games: [Game.Shareable]?
		public var scores: [Game.ID: ScoredGame] = [:]
		public var isGameIncluded: IdentifiedArrayOf<GameWithInclude> = []
		public var isShowingGameTitles: Bool = true
		public var isShowingSeriesDetails: Bool = true
		public var isShowingSeriesDate: Bool = true
		public var isShowingBowlerName: Bool = false
		public var isShowingLeagueName: Bool = false
		public var style: ShareableGamesImage.Configuration.Style = .plain

		public var displayScale: CGFloat = .zero
		public var preferredAppearance: Appearance = .dark

		public var errors: Errors<ErrorID>.State = .init()

		var configuration: ShareableGamesImage.Configuration? {
			guard let games, games.count == scores.count else { return nil }
			return .init(
				scores: games
					.compactMap { scores[$0.id] }
					.filter { isGameIncluded[id: $0.id]?.isIncluded == true },
				isShowingGameTitles: isShowingGameTitles,
				isShowingSeriesDetails: isShowingSeriesDetails,
				bowlerName: isShowingBowlerName ? series?.bowlerName : nil,
				leagueName: isShowingLeagueName ? series?.leagueName : nil,
				date: isShowingSeriesDate ? series?.date : nil,
				total: isShowingSeriesDetails ? series?.total : nil,
				style: style,
				displayScale: displayScale,
				colorScheme: preferredAppearance.colorScheme
			)
		}

		public init(seriesId: Series.ID) {
			self.seriesId = seriesId
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case task
			case didUpdateDisplayScale(CGFloat)
			case didUpdateColorScheme(ColorScheme)
		}
		@CasePathable
		public enum Delegate {
			case imageRendered(UIImage)
		}
		@CasePathable
		public enum Internal {
			case loadSeriesResponse(Result<Series.Shareable, Error>)
			case loadGamesResponse(Result<[Game.Shareable], Error>)
			case loadScoreResponse(Result<ScoredGame, Error>)

			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum ErrorID: Hashable {
		case seriesNotFound
		case gamesNotFound
		case failedToCalculateScore
	}

	public enum CancelID: Hashable, Sendable {
		case scoreKeeper
		case imageRenderer
	}

	public struct GameWithInclude: Identifiable, Equatable {
		public let id: Game.ID
		public let ordinal: Int
		public var isIncluded: Bool
	}

	public init() {}

	@Dependency(GamesRepository.self) var games
	@Dependency(ScoresRepository.self) var scores
	@Dependency(SeriesRepository.self) var series

	public var body: some ReducerOf<Self> {
		CombineReducers {
			BindingReducer()

			Reduce<State, Action> { state, action in
				switch action {
				case let .view(viewAction):
					switch viewAction {
					case .task:
						return .merge(
							.run { [seriesId = state.seriesId] send in
								await send(.internal(.loadSeriesResponse(Result {
									try await series.shareable(seriesId)
								})))
							},
							.run { [seriesId = state.seriesId] send in
								await send(.internal(.loadGamesResponse(Result {
									try await games.shareSeries(seriesId)
								})))
							}
						)

					case let .didUpdateDisplayScale(displayScale):
						state.displayScale = displayScale
						return .none

					case let .didUpdateColorScheme(colorScheme):
						switch colorScheme {
						case .dark: state.preferredAppearance = .dark
						case .light: state.preferredAppearance = .light
						@unknown default: state.preferredAppearance = .light
						}
						return .none
					}

				case let .internal(internalAction):
					switch internalAction {
					case let .loadScoreResponse(.success(score)):
						state.scores[score.id] = score
						return .none

					case let .loadSeriesResponse(.success(series)):
						state.series = series
						return .none

					case let .loadGamesResponse(.success(games)):
						state.games = games
						state.isGameIncluded = games
							.map { GameWithInclude(id: $0.id, ordinal: $0.index + 1, isIncluded: true) }
							.eraseToIdentifiedArray()
						return .merge(
							games.map { game in
									.run { send in
										for try await score in scores.observeScore(for: game.id) {
											await send(.internal(.loadScoreResponse(.success(score))))
										}
									} catch: { error, send in
										await send(.internal(.loadScoreResponse(.failure(error))))
									}
							}
						)
						.cancellable(id: CancelID.scoreKeeper, cancelInFlight: true)

					case let .loadSeriesResponse(.failure(error)):
						return state.errors
							.enqueue(.seriesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
							.map { .internal(.errors($0)) }

					case let .loadGamesResponse(.failure(error)):
						return state.errors
							.enqueue(.gamesNotFound, thrownError: error, toastMessage: Strings.Error.Toast.dataNotFound)
							.map { .internal(.errors($0)) }

					case let .loadScoreResponse(.failure(error)):
						return state.errors
							.enqueue(.failedToCalculateScore, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
							.map { .internal(.errors($0)) }

					case .errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
						return .none
					}

				case .delegate, .binding:
					return .none
				}
			}
		}
		.onChange(of: \.configuration) { _, configuration in
			Reduce<State, Action> { _, _ in
				.run { @MainActor send in
					guard let configuration else {  return }
					let imageRenderer = ImageRenderer(
						content: ShareableGamesImage(configuration: configuration)
							.frame(minWidth: 900)
							.environment(\.colorScheme, configuration.colorScheme)
					)
					imageRenderer.scale = configuration.displayScale

					guard let image = imageRenderer.uiImage else {
						return
					}

					guard !Task.isCancelled else { return }

					send(.delegate(.imageRendered(image)))
				}
				.cancellable(id: CancelID.imageRenderer, cancelInFlight: true)
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.loadSeriesResponse(.failure(error))),
					let .internal(.loadGamesResponse(.failure(error))),
					let .internal(.loadScoreResponse(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

// MARK: - View

@ViewAction(for: GamesSharing.self)
public struct GamesSharingView: View {
	@Bindable public var store: StoreOf<GamesSharing>

	@Environment(\.colorScheme) var colorScheme
	@Environment(\.displayScale) var displayScale

	public init(store: StoreOf<GamesSharing>) {
		self.store = store
	}

	public var body: some View {
		List {
			detailsSection
			gamesSection
			appearanceSection
		}
		.task { await send(.task).finish() }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.onAppear {
			send(.didUpdateColorScheme(colorScheme))
			send(.didUpdateDisplayScale(displayScale))
		}
		.onChange(of: displayScale) { send(.didUpdateDisplayScale(displayScale)) }
	}

	private var detailsSection: some View {
		Section {
			Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .smallSpacing) {
				GridRow {
					ChipButton(
						icon: .calendar,
						title: Strings.Sharing.Game.Details.date,
						isOn: $store.isShowingSeriesDate.animation(.easeInOut(duration: 0.2))
					)

					ChipButton(
						icon: .listDash,
						title: Strings.Sharing.Game.Details.scoreSummary,
						isOn: $store.isShowingSeriesDetails.animation(.easeInOut(duration: 0.2))
					)
				}

				GridRow {
					ChipButton(
						icon: .personFill,
						title: Strings.Sharing.Game.Details.bowlerName,
						isOn: $store.isShowingBowlerName.animation(.easeInOut(duration: 0.2))
					)

					ChipButton(
						icon: .repeat,
						title: Strings.Sharing.Game.Details.leagueName,
						isOn: $store.isShowingLeagueName.animation(.easeInOut(duration: 0.2))
					)
				}
			}
		}
		.listRowSeparator(.hidden)
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color.clear)
	}

	private var gamesSection: some View {
		Section {
			DisclosureGroup {
				ForEach($store.isGameIncluded) { $isIncluded in
					Toggle(
						Strings.Game.titleWithOrdinal(isIncluded.ordinal),
						isOn: $isIncluded.isIncluded
					)
				}
			} label: {
				Text(Strings.Sharing.Game.Details.Games.title)
			}
		}
	}

	private var appearanceSection: some View {
		Section(Strings.Sharing.Game.Details.Appearance.title) {
			Picker(
				Strings.Sharing.Game.Details.ColorPalette.title,
				selection: $store.style
			) {
				ForEach(ShareableGamesImage.Configuration.Style.allCases) { style in
					Text(style.title)
						.tag(style)
				}
			}

			AppearancePicker(selection: $store.preferredAppearance)
		}
	}
}

extension ShareableGamesImage.Configuration.Style {
	var title: String {
		switch self {
		case .grayscale: Strings.Sharing.Game.Details.ColorPalette.grayscale
		case .plain: Strings.Sharing.Game.Details.ColorPalette.plain
		}
	}
}

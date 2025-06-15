import Algorithms
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
		public var selectedGame: Game.ID?
		public var layout: Layout = .horizontal
		public var isGameIncluded: IdentifiedArrayOf<GameWithInclude> = []
		public var isShowingGameTitles: Bool = true
		public var isShowingSeriesDetails: Bool = true
		public var isShowingSeriesDate: Bool = true
		public var isShowingBowlerName: Bool = false
		public var isShowingLeagueName: Bool = false
		public var style: ShareableGamesImage.Style = .plain

		public var displayScale: CGFloat = .zero
		public var preferredAppearance: Appearance = .dark

		public var errors: Errors<ErrorID>.State = .init()

		var horizontalConfiguration: HorizontalShareableGamesImage.Configuration? {
			guard layout == .horizontal else { return nil }

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

		var verticalConfiguration: VerticalShareableGameImage.Configuration? {
			guard layout == .rectangular else { return nil }

			guard let selectedGame, let game = scores[selectedGame] else { return nil }

			return .init(
				score: game,
				style: style,
				bowlerName: isShowingBowlerName ? series?.bowlerName : nil,
				leagueName: isShowingLeagueName ? series?.leagueName : nil,
				date: isShowingSeriesDate ? series?.date : nil,
				displayScale: displayScale,
				colorScheme: preferredAppearance.colorScheme
			)
		}

		public init(seriesId: Series.ID, gameId: Game.ID?) {
			self.seriesId = seriesId
			self.selectedGame = gameId
			self.layout = gameId == nil ? .horizontal : .rectangular
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

	public enum ErrorID: Hashable, Sendable {
		case seriesNotFound
		case gamesNotFound
		case failedToCalculateScore
	}

	public enum CancelID: Hashable, Sendable {
		case scoreKeeper
		case verticalImageRenderer
		case horizontalImageRenderer
	}

	public struct GameWithInclude: Identifiable, Equatable {
		public let id: Game.ID
		public let ordinal: Int
		public var isIncluded: Bool
	}

	public enum Layout: CaseIterable, Identifiable, Hashable, Sendable {
		case horizontal
		case rectangular

		public var id: Self { self }
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
						state.selectedGame = games.first?.id
						state.layout = games.count > 1 ? .horizontal : .rectangular
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
		.onChange(of: \.verticalConfiguration) { _, verticalConfiguration in
			Reduce<State, Action> { _, _ in
				.run { @MainActor send in
					guard let verticalConfiguration else { return }
					let imageRenderer = ImageRenderer(
						content: VerticalShareableGameImage(configuration: verticalConfiguration)
							.frame(minWidth: 900, minHeight: 400)
							.background(.red)
							.environment(\.colorScheme, verticalConfiguration.colorScheme)
					)
					imageRenderer.scale = verticalConfiguration.displayScale

					guard let image = imageRenderer.uiImage else {
						return
					}

					guard !Task.isCancelled else { return }

					try? image.pngData()?.write(to: FileManager.default.temporaryDirectory.appendingPathComponent("image.png"))

					send(.delegate(.imageRendered(image)))
				}
				.cancellable(id: CancelID.verticalImageRenderer, cancelInFlight: true)
			}
		}
		.onChange(of: \.horizontalConfiguration) { _, horizontalConfiguration in
			Reduce<State, Action> { _, _ in
				.run { @MainActor send in
					guard let horizontalConfiguration else {  return }
					let imageRenderer = ImageRenderer(
						content: HorizontalShareableGamesImage(configuration: horizontalConfiguration)
							.frame(minWidth: 900)
							.environment(\.colorScheme, horizontalConfiguration.colorScheme)
					)
					imageRenderer.scale = horizontalConfiguration.displayScale

					guard let image = imageRenderer.uiImage else {
						return
					}

					guard !Task.isCancelled else { return }

					send(.delegate(.imageRendered(image)))
				}
				.cancellable(id: CancelID.horizontalImageRenderer, cancelInFlight: true)
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
			let buttons: [Buttons] = switch store.layout {
			case .horizontal: [.date, .summary, .league, .bowler]
			case .rectangular: [.date, .league, .bowler]
			}
			let buttonRows: [[Buttons]] = buttons.chunks(ofCount: 2).map { Array($0) }

			Grid(horizontalSpacing: .smallSpacing, verticalSpacing: .smallSpacing) {
				ForEach(buttonRows, id: \.first?.id) { row in
					GridRow {
						ForEach(row, id: \.id) { type in
							button(for: type)
						}
					}
				}
			}
		}
		.listRowSeparator(.hidden)
		.listRowInsets(EdgeInsets())
		.listRowBackground(Color.clear)
	}

	// MARK: Buttons

	enum Buttons: Identifiable, Hashable {
		case date
		case summary
		case bowler
		case league

		var id: Self { self }
	}

	@ViewBuilder
	private func button(for type: Buttons) -> some View {
		switch type {
		case .date: dateButton
		case .summary: summaryButton
		case .bowler: bowlerButton
		case .league: leagueButton
		}
	}

	private var dateButton: some View {
		ChipButton(
			systemImage: "calendar",
			title: Strings.Sharing.Game.Details.date,
			isOn: $store.isShowingSeriesDate.animation(.easeInOut(duration: 0.2))
		)
	}

	private var summaryButton: some View {
		ChipButton(
			systemImage: "list.dash",
			title: Strings.Sharing.Game.Details.scoreSummary,
			isOn: $store.isShowingSeriesDetails.animation(.easeInOut(duration: 0.2))
		)
	}

	private var bowlerButton: some View {
		ChipButton(
			systemImage: "person.fill",
			title: Strings.Sharing.Game.Details.bowlerName,
			isOn: $store.isShowingBowlerName.animation(.easeInOut(duration: 0.2))
		)
	}

	private var leagueButton: some View {
		ChipButton(
			systemImage: "repeat",
			title: Strings.Sharing.Game.Details.leagueName,
			isOn: $store.isShowingLeagueName.animation(.easeInOut(duration: 0.2))
		)
	}

	// MARK: Games

	private var gamesSection: some View {
		Section {
			switch store.layout {
			case .horizontal:
				gamesSelection
			case .rectangular:
				gamePicker
			}
		}
	}

	private var gamesSelection: some View {
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

	private var gamePicker: some View {
		Picker(
			Strings.Sharing.Game.Details.SelectedGame.title,
			selection: $store.selectedGame
		) {
			ForEach(store.games ?? []) { game in
				Text(Strings.Game.titleWithOrdinal(game.index + 1))
					.tag(game.id)
			}
		}
	}

	// MARK: Appearance

	private var appearanceSection: some View {
		Section(Strings.Sharing.Game.Details.Appearance.title) {
			VStack(alignment: .leading) {
				Picker(
					Strings.Sharing.Game.Details.Layout.title,
					selection: $store.layout
				) {
					ForEach(GamesSharing.Layout.allCases) { layout in
						Text(layout.title)
							.tag(layout)
					}
				}

				Text(Strings.Sharing.Game.Details.Layout.description)
					.font(.caption)
					.frame(maxWidth: .infinity, alignment: .leading)
			}

			Picker(
				Strings.Sharing.Game.Details.ColorPalette.title,
				selection: $store.style
			) {
				ForEach(ShareableGamesImage.Style.allCases) { style in
					Text(style.title)
						.tag(style)
				}
			}

			AppearancePicker(selection: $store.preferredAppearance)
		}
	}
}

// MARK: - Strings

extension ShareableGamesImage.Style {
	var title: String {
		switch self {
		case .grayscale: Strings.Sharing.Game.Details.ColorPalette.grayscale
		case .plain: Strings.Sharing.Game.Details.ColorPalette.plain
		}
	}
}

extension GamesSharing.Layout {
	var title: String {
		switch self {
		case .horizontal: Strings.Sharing.Game.Details.Layout.horizontal
		case .rectangular: Strings.Sharing.Game.Details.Layout.rectangular
		}
	}
}

// MARK: - Preview

#Preview {
	GamesSharingView(store: Store(
		initialState: GamesSharing.State(seriesId: UUID(), gameId: UUID()),
		reducer: { GamesSharing() }
	))
}

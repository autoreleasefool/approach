import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import DateTimeLibrary
import EquatablePackageLibrary
import FeatureActionLibrary
import FeatureFlagsLibrary
import FormFeature
import Foundation
import LanesRepositoryInterface
import MapKit
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StringsLibrary
import SwiftUI

public typealias SeriesForm = FormFeature.Form<Series.Create, Series.Edit>

@Reducer
// swiftlint:disable:next type_body_length
public struct SeriesEditor: Reducer {
	@ObservableState
	public struct State: Equatable {
		public let league: League.SeriesHost

		public var numberOfGames: Int
		public var date: Date
		public var isUsingPreBowl: Bool
		public var appliedDate: Date
		public var preBowl: Series.PreBowl
		public var excludeFromStatistics: Series.ExcludeFromStatistics
		public var mapPosition: MapCameraPosition
		public var location: Alley.Summary?
		public var isCreatingManualSeries: Bool = false
		public var manualScores: IdentifiedArrayOf<ManualSeriesGameEditor.State> = []

		public let initialValue: SeriesForm.Value
		public var form: SeriesForm.State

		public var isPreBowlFormEnabled: Bool
		public var isManualSeriesEnabled: Bool

		var isExcludeFromStatisticsToggleEnabled: Bool {
			(preBowl == .preBowl && !isUsingPreBowl) || league.excludeFromStatistics == .exclude
		}

		var isDismissDisabled: Bool { alleyPicker != nil }
		var isEditing: Bool {
			switch initialValue {
			case .create: false
			case .edit: true
			}
		}

		@Presents public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: InitialValue, inLeague: League.SeriesHost) {
			@Dependency(\.date) var date

			self.league = inLeague
			switch value {
			case let .create(new):
				self.numberOfGames = new.numberOfGames
				self.date = new.date
				self.appliedDate = new.appliedDate ?? date()
				self.isUsingPreBowl = new.isPreBowlUsed
				self.preBowl = new.preBowl
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.mapPosition = .automatic
				self.initialValue = .create(new)
			case let .edit(existing):
				self.numberOfGames = existing.numberOfGames
				self.date = existing.date
				self.appliedDate = existing.appliedDate ?? date()
				self.preBowl = existing.preBowl
				self.isUsingPreBowl = existing.isPreBowlUsed
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				self.mapPosition = existing.location?.location?.coordinate.mapPosition ?? .automatic
				self.initialValue = .edit(existing)
			}
			self.form = .init(initialValue: self.initialValue)

			@Dependency(\.featureFlags) var featureFlags
			self.isPreBowlFormEnabled = featureFlags.isFlagEnabled(.preBowlForm)
			self.isManualSeriesEnabled = featureFlags.isFlagEnabled(.manualSeries)
		}

		mutating func syncFormSharedState() {
			switch initialValue {
			case var .create(new):
				new.date = date
				new.preBowl = preBowl
				new.appliedDate = isUsingPreBowl ? appliedDate : nil
				new.excludeFromStatistics = (preBowl == .preBowl && !isUsingPreBowl) ? .exclude : excludeFromStatistics
				new.numberOfGames = numberOfGames
				new.location = location
				new.manualScores = isCreatingManualSeries ? manualScores.map { $0.score } : nil
				form.value = .create(new)
			case var .edit(existing):
				existing.date = date
				existing.appliedDate = isUsingPreBowl ? appliedDate : nil
				existing.preBowl = preBowl
				existing.excludeFromStatistics = (preBowl == .preBowl && !isUsingPreBowl) ? .exclude : excludeFromStatistics
				existing.location = location
				form.value = .edit(existing)
			}
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didTapAlley
		}
		@CasePathable public enum Delegate {
			case didFinishCreating(Series.Create)
			case didFinishArchiving(Series.Edit)
			case didFinishUpdating(Series.Edit)
		}
		@CasePathable public enum Internal {
			case form(SeriesForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
			case manualSeriesGame(IdentifiedActionOf<ManualSeriesGameEditor>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public enum InitialValue {
		case create(Series.Create)
		case edit(Series.Edit)
	}

	public init() {}

	@Dependency(AlleysRepository.self) var alleys
	@Dependency(\.calendar) var calendar
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(SeriesRepository.self) var series
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.form, action: \.internal.form) {
			SeriesForm()
				.dependency(RecordPersistence(
					create: series.create,
					update: series.update,
					delete: { _ in },
					archive: series.archive
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .manualSeriesGame(.element(_, .delegate(.doNothing))):
					return .none

				case .manualSeriesGame(.element(_, .binding(\.score))):
					state.syncFormSharedState()
					return .none

				case let .alleyPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						state.mapPosition = state.location?.location?.coordinate.mapPosition ?? .automatic
						state.syncFormSharedState()
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state.form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state.form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didArchive(result):
						return state.form.didFinishArchiving(result)
							.map { .internal(.form($0)) }

					case let .didFinishCreating(series):
						return .concatenate(
							.send(.delegate(.didFinishCreating(series))),
							.run { _ in await dismiss() }
						)

					case let .didFinishArchiving(series):
						return .concatenate(
							.send(.delegate(.didFinishArchiving(series))),
							.run { _ in await dismiss() }
						)

					case let .didFinishUpdating(series):
						return .concatenate(
							.send(.delegate(.didFinishUpdating(series))),
							.run { _ in await dismiss() }
						)

					case .didDiscard, .didDelete, .didFinishDeleting:
						return .run { _ in await dismiss() }
					}

				case .form(.view), .form(.internal),
						.alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss),
						.manualSeriesGame(.element(_, .view)), .manualSeriesGame(.element(_, .internal)),
						.manualSeriesGame(.element(_, .binding)):
					return .none
				}

			case .binding(\.date):
				state.date = calendar.startOfDay(for: state.date)
				state.syncFormSharedState()
				return .none

			case .binding(\.excludeFromStatistics):
				switch (state.league.excludeFromStatistics, state.preBowl) {
				case (.exclude, _):
					state.excludeFromStatistics = .exclude
				case (_, .preBowl):
					if !state.isUsingPreBowl {
						state.excludeFromStatistics = .exclude
					}
				case (.include, .regular):
					break
				}
				state.syncFormSharedState()
				return .none

			case .binding(\.preBowl):
				switch (state.league.excludeFromStatistics, state.preBowl) {
				case (.exclude, _):
					state.excludeFromStatistics = .exclude
				case (_, .preBowl):
					if !state.isUsingPreBowl {
						state.excludeFromStatistics = .exclude
					}
				case (.include, .regular):
					state.excludeFromStatistics = .include
					state.isUsingPreBowl = false
				}
				state.syncFormSharedState()
				return .none

			case .binding(\.isUsingPreBowl):
				switch (state.preBowl, state.isUsingPreBowl) {
				case (.preBowl, false):
					state.excludeFromStatistics = .exclude
				case (.preBowl, true), (.regular, _):
					break
				}
				state.syncFormSharedState()
				return .none

			case .binding(\.isCreatingManualSeries):
				if state.isCreatingManualSeries {
					state.manualScores = .init(uniqueElements: (0..<state.numberOfGames).map {
						ManualSeriesGameEditor.State(id: uuid(), index: $0)
					})
				} else {
					state.manualScores = []
				}
				state.syncFormSharedState()
				return .none

			case .binding(\.numberOfGames):
				if state.numberOfGames < state.manualScores.count {
					state.manualScores.removeLast(state.manualScores.count - state.numberOfGames)
				} else if state.numberOfGames > state.manualScores.count {
					state.manualScores.append(
						contentsOf: (state.manualScores.count..<state.numberOfGames).map { .init(id: uuid(), index: $0) }
					)
				}
				state.syncFormSharedState()
				return .none

			case .binding:
				state.syncFormSharedState()
				return .none

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: \.internal.alleyPicker) {
			ResourcePicker { _ in
				alleys.pickable()
			}
		}
		.forEach(\.manualScores, action: \.internal.manualSeriesGame) {
			ManualSeriesGameEditor()
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.Series.Created()
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.Series.Updated()
			case .internal(.form(.delegate(.didFinishArchiving))):
				return Analytics.Series.Archived()
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
}

extension Series.Create: CreateableRecord {
	public static var modelName = Strings.Series.title
	public static var isSaveableWithoutChanges: Bool { true }

	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
	public var saveButtonText: String { Strings.Action.start }
}

extension Series.Edit: EditableRecord {
	public var isDeleteable: Bool { false }
	public var isArchivable: Bool {
		switch leagueRecurrence {
		case .once: return false
		case .repeating: return true
		}
	}
	public var isSaveable: Bool { true }
	public var name: String { date.longFormat }
}

extension Location.Coordinate {
	var mapPosition: MapCameraPosition {
		.region(.init(center: mapCoordinate, latitudinalMeters: 200, longitudinalMeters: 200))
	}
}

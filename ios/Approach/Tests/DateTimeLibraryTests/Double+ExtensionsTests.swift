import DateTimeLibrary
import Testing

@Suite("Double Extensions", .tags(.extensions, .library))
struct DoubleExtensionTests {

	@Suite("durationFormat", .tags(.unit))
	struct DurationFormatTests {
		@Test(
			"Formats correctly",
			.tags(.extensions, .unit),
			arguments: zip(
				[1_051_380, 86_220, 1_440, 360],
				["292:03", "23:57", "0:24", "0:06"]
			)
		)
		func durationFormat(duration: Double, expectedDurationFormat: String) {
			#expect(duration.durationFormat == expectedDurationFormat)
		}

		@Test(
			"Ignores seconds",
			.tags(.unit),
			arguments: zip(
				/* 1051401 == 292:03:21 */
				/* 86235 == 23:57:15 */
				/* 362 = 0:06:02 */
				[1_051_401, 86_235, 362],
				["292:03", "23:57", "0:06"]
			)
		)
		func ignoresSeconds(duration: Double, expectedDurationFormat: String) {
			#expect(duration.durationFormat == expectedDurationFormat)
		}
	}
}
